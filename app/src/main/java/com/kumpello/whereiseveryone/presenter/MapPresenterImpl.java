package com.kumpello.whereiseveryone.presenter;


import android.Manifest;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.CameraPosition;
import com.kumpello.whereiseveryone.model.MapService;
import com.kumpello.whereiseveryone.model.PermissionHandler;
import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.model.UserService;
import com.kumpello.whereiseveryone.mvp.BasePresenter;
import com.kumpello.whereiseveryone.utils.CallbackIterator;
import com.kumpello.whereiseveryone.utils.OnResult;
import com.kumpello.whereiseveryone.utils.SimpleTimer;
import com.kumpello.whereiseveryone.view.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapPresenterImpl extends BasePresenter<MapView> implements MapPresenter {
    private final MapService mapService;
    private final PermissionHandler permissionHandler;
    private final List<String> permissionsNeeded;
    private boolean userMarkerPlaced;
    private final SimpleTimer timer;
    private final UserService userService;
    private final User user;
    private boolean userExists;
    private final List<User> friends;
    private LatLng userLatLng;
    //Get this field to common settings file
    private static final float INITIAL_ZOOM = 18;


    public MapPresenterImpl(MapService mapService, PermissionHandler permissionHandler, UserService userService, SimpleTimer timer) {
        permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        this.mapService = mapService;
        this.permissionHandler = permissionHandler;
        userMarkerPlaced = false;
        this.userService = userService;
        this.timer = timer;
        user = new User(userService.getToken(), userService.getEmail(), userService.getUserType());
        userExists = userService.userExists();
        friends = new ArrayList<>();
        getFriendList();
    }

    @Override
    public void addNotification(String text) {
        userService.addNotification(text);
    }

    @Override
    public void getNotification(@NonNull OnResult<String> handler) {
        userService.getNotification(handler);
    }

    @Override
    public void getFriendList() {
        Log.d("MapPresenter: getting friend list", String.valueOf(friends.size()));
        userService.getFriendsList(new CallbackIterator<>() {
            @Override
            public void onNext(User result) {
                Log.d("MapPresenter: User got, checking friendship", result.userID);
                userService.checkFriendship(result, new OnResult<>() {
                    @Override
                    public void onSuccess(Boolean friendShipResult) {
                        Log.d("MapPresenter: result:", String.valueOf(friendShipResult));
                        if (friendShipResult) {
                            Log.d("MapPresenter: friendship is true", result.userID);
                            friends.add(result);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }

    @Override
    public boolean updateUserLocationAndDirection() {
        if (mapService.updateUserLocationAndDirection()) {
            user.userLocation = getUserLatLng();
            user.userAzimuth = getAzimuth();
            if (userExists) {
                userService.updateUserLocationAndDirection(user);
                Log.d("MapPresenter:updating user location", user.nick);
            } else {
                userService.firstRun(true);
                userService.updateUserOnServer(user);
                Log.d("MapPresenter:updating user on server", user.nick);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getAzimuth() {
        return mapService.getAzimuth();
    }

    @Override
    public LatLng getUserLatLng() {
        Location userLocation = mapService.getLocation();
        userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        return userLatLng;
    }

    @Override
    public CameraPosition getBaseCameraPosition() {
        return new CameraPosition.Builder()
                .target(userLatLng)
                .zoom(INITIAL_ZOOM)
                .tilt(20)
                .bearing(getAzimuth())
                .build();
    }

    @Override
    public void startLocationUpdates() {
        checkPermissions(permissionsNeeded);
        mapService.startLocationUpdates();
        ArrayList<String> friendsMarkersPlaced = new ArrayList<>();

        timer.start(() -> {
            if (mapService.locationCallbackReady()) {
                if (!userMarkerPlaced) {
                    view.addUserMarker();
                    userMarkerPlaced = true;
                    view.centerCamera();
                }
                for (User user : friends) {
                    if (!friendsMarkersPlaced.contains(user.userID)) {
                        friendsMarkersPlaced.add(user.userID);
                        view.addFriendsMarker(user);
                    }
                }
                view.updateUserLocation();
                userService.updateFriendsList(friends, new CallbackIterator<>() {
                    @Override
                    public void onNext(User result) {
                        view.updateFriendsLocation(result);
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });
            }
        }, 1000);
    }

    @Override
    public void stopLocationUpdates() {
        timer.stop();
        mapService.stopLocationUpdates();
    }

    @Override
    public void onStop() {
        timer.changeInterval(60000);
    }

    @Override
    public void onPause() {
        timer.changeInterval(5000);
        mapService.onPause();
    }

    @Override
    public void onResume() {
        timer.changeInterval(1000);
        mapService.onResume();
    }

    public void updateLastLocation() {
        mapService.updateLastLocation();
    }

    @Override
    public void checkPermissions(List<String> permissions) {
        List<String> notGrantedPermissions = permissionHandler.arePermissionGranted(permissions);
        if (notGrantedPermissions != null) {
            for (String permission : permissions) {
                if (permissionHandler.askForPermission(permission)) {
                    view.showPermissionDialog(permission);
                } else {
                    view.requestPermission(permission);
                }
            }
        }
    }


}

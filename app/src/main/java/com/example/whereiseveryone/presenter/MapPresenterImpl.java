package com.example.whereiseveryone.presenter;


import android.Manifest;
import android.location.Location;

import com.example.whereiseveryone.model.MapService;
import com.example.whereiseveryone.model.PermissionHandler;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.model.UserService;
import com.example.whereiseveryone.mvp.BasePresenter;
import com.example.whereiseveryone.utils.CallbackIterator;
import com.example.whereiseveryone.utils.OnResult;
import com.example.whereiseveryone.utils.SimpleTimer;
import com.example.whereiseveryone.view.MapView;
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
    private final boolean userExists;
    private final List<User> friends;


    public MapPresenterImpl(MapService mapService, PermissionHandler permissionHandler, UserService userService, SimpleTimer timer) {
        permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        this.mapService = mapService;
        this.permissionHandler = permissionHandler;
        userMarkerPlaced = false;
        this.userService = userService;
        this.timer = timer;
        user = new User(userService.getToken(), userService.getEmail());
        userExists = userService.userExists();
        friends = new ArrayList<>();
        getFriendList();
    }

    @Override
    public void getFriendList() {
        userService.getFriendsList(new CallbackIterator<User>() {
            @Override
            public void onNext(User result) {
                userService.checkFriendship(result, new OnResult<Boolean>() {
                    @Override
                    public void onSuccess(Boolean friendShipResult) {
                        if (friendShipResult) {
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
            } else {
                userService.updateUserOnServer(user);
            }
            return true;
        } else {
            return false;
        }
    }

    public float getAzimuth() {
        return mapService.getAzimuth();
    }

    @Override
    public LatLng getUserLatLng() {
        Location userLocation = mapService.getLocation();
        return new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
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
                userService.updateFriendsList(friends, new CallbackIterator<User>() {
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
    public void onPause() {
        mapService.onPause();
    }

    @Override
    public void onResume() {
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

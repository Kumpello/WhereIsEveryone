package com.example.whereiseveryone.presenter;


import android.Manifest;
import android.location.Location;

import com.example.whereiseveryone.model.MapService;
import com.example.whereiseveryone.model.PermissionHandler;
import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.model.UserService;
import com.example.whereiseveryone.mvp.BasePresenter;
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
    private UserService userService;
    private User user;


    public MapPresenterImpl(MapService mapService, PermissionHandler permissionHandler, UserService userService, SimpleTimer timer) {
        permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        this.mapService = mapService;
        this.permissionHandler = permissionHandler;
        userMarkerPlaced = false;
        this.userService = userService;
        this.timer = timer;
    }


    @Override
    public boolean updateUserLocationAndDirection() {
        return mapService.updateUserLocationAndDirection();
    }

    public float getAzimuth(){
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

        timer.start(() -> {
            if (mapService.locationCallbackReady()) {
                if(!userMarkerPlaced){
                    view.addUserMarker();
                    userMarkerPlaced = true;
                }
                view.updateUserLocation();
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

    public void updateLastLocation(){
        mapService.updateLastLocation();
    }

    @Override
    public void checkPermissions(List<String> permissions) {
        List<String> notGrantedPermissions = permissionHandler.arePermissionGranted(permissions);
        if(notGrantedPermissions != null) {
            for (String permission : permissions) {
                if(permissionHandler.askForPermission(permission)) {
                    view.showPermissionDialog(permission);
                } else {
                    view.requestPermission(permission);
                }
            }
        }
    }


}

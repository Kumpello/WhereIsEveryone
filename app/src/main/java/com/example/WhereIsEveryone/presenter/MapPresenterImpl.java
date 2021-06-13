package com.example.WhereIsEveryone.presenter;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;

import com.example.WhereIsEveryone.model.MapService;
import com.example.WhereIsEveryone.model.PermissionHandler;
import com.example.WhereIsEveryone.mvp.BasePresenter;
import com.example.WhereIsEveryone.view.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MapPresenterImpl extends BasePresenter<MapView> implements MapPresenter {
    private MapService mapService;
    private PermissionHandler permissionHandler;
    private List<String> permissionsNeeded;
    Timer timer;


    public MapPresenterImpl(MapService mapService, PermissionHandler permissionHandler) {
        permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        this.mapService = mapService;
        this.permissionHandler = permissionHandler;
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
    }

    @Override
    public void stopLocationUpdates() {
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

    public void getLastLocation(){
        mapService.getLastLocation();
    }

    @Override
    public void checkPermissions(List<String> permissions) {
        List<String> notGrantedPermissions = permissionHandler.arePermissionGranted(permissions);
        if(notGrantedPermissions.equals(null)) {
            return;
        } else {
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

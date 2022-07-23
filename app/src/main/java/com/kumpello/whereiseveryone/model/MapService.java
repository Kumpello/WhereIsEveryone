package com.kumpello.whereiseveryone.model;

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;

public interface MapService {
    boolean updateUserLocationAndDirection();
    boolean locationCallbackReady();
    void updateLastLocation();
    float getAzimuth();
    Location getLocation();
    void startLocationUpdates();
    void stopLocationUpdates();
    void onPause();
    void onResume();
}

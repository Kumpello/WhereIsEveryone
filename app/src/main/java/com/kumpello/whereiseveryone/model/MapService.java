package com.kumpello.whereiseveryone.model;

import android.location.Location;

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

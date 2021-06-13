package com.example.WhereIsEveryone.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public interface MapService {
    boolean updateUserLocationAndDirection();
    void getLastLocation();
    float getAzimuth();
    Location getLocation();
    void startLocationUpdates();
    void stopLocationUpdates();
    void onPause();
    void onResume();
}

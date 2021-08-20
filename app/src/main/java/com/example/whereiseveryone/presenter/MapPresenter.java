package com.example.whereiseveryone.presenter;

import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.Contract;
import com.example.whereiseveryone.view.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MapPresenter extends Contract.Presenter<MapView> {
    boolean updateUserLocationAndDirection();
    void checkPermissions(List<String> permissions);
    void updateLastLocation();
    float getAzimuth();
    LatLng getUserLatLng();
    void startLocationUpdates();
    void stopLocationUpdates();
    void onPause();
    void onResume();
    String getToken();
}

package com.example.WhereIsEveryone.presenter;

import com.example.WhereIsEveryone.mvp.Contract;
import com.example.WhereIsEveryone.view.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MapPresenter extends Contract.Presenter<MapView> {
    boolean updateUserLocationAndDirection();
    void checkPermissions(List<String> permissions);
    void getLastLocation();
    float getAzimuth();
    LatLng getUserLatLng();
    void startLocationUpdates();
    void stopLocationUpdates();
    void onPause();
    void onResume();
}

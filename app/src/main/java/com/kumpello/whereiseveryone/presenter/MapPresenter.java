package com.kumpello.whereiseveryone.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.view.MapView;

import java.util.List;

public interface MapPresenter extends Contract.Presenter<MapView> {
    boolean updateUserLocationAndDirection();
    void checkPermissions(List<String> permissions);
    void updateLastLocation();
    float getAzimuth();
    LatLng getUserLatLng();
    void startLocationUpdates();
    void stopLocationUpdates();
    void getFriendList();
    void onPause();
    void onResume();
    void onStop();
}

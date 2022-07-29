package com.kumpello.whereiseveryone.presenter;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.kumpello.whereiseveryone.mvp.Contract;
import com.kumpello.whereiseveryone.utils.OnResult;
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
    void addNotification(String text);
    void getNotification(@NonNull OnResult<String> handler);
    LatLng getLastPosition();
    CameraPosition getBaseCameraPosition();
}

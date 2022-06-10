package com.kumpello.whereiseveryone.view;

import com.kumpello.whereiseveryone.model.User;
import com.kumpello.whereiseveryone.mvp.Contract;

public interface MapView extends Contract.View {
    void centerCamera();
    void updateUserLocation();
    void showPermissionDialog(String permission);
    void requestPermission(String permission);
    void addUserMarker();
    void addFriendsMarker(User user);
    void updateFriendsLocation(User user);
}

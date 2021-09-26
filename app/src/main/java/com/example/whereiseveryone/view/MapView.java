package com.example.whereiseveryone.view;

import com.example.whereiseveryone.model.User;
import com.example.whereiseveryone.mvp.Contract;

public interface MapView extends Contract.View {
    void centerCamera();
    void updateUserLocation();
    void showPermissionDialog(String permission);
    void requestPermission(String permission);
    void addUserMarker();
    void addFriendsMarker(User user);
    void updateFriendsLocation(User user);
}

package com.example.WhereIsEveryone.view;

import com.example.WhereIsEveryone.mvp.Contract;

public interface MapView extends Contract.View {
    void centerCamera();
    void updateUserLocation();
    void showPermissionDialog(String permission);
    void requestPermission(String permission);
}

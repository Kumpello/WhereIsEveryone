package com.example.WhereIsEveryone.model;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;


import java.util.List;

public class PermissionHandlerImpl implements PermissionHandler {

    private Activity activity;

    public PermissionHandlerImpl(Activity activity) {
        this.activity = activity;
    }


    @Override
    public boolean askForPermission(String permission) {
        if (shouldShowRequestPermissionRationale(activity, permission)) {
             return true;
        } else {
             return false;
        }
    }

    //Returns not granted permissions
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public List<String> arePermissionGranted(List<String> required) {
        for (int i = 0; i < required.size(); i++) {
            if (ContextCompat.checkSelfPermission(activity,
                    required.get(i)) == PackageManager.PERMISSION_GRANTED) {
                required.remove(i);
            }
        }
        if (required.size() == 0){
            return null;
        }
        return required;
    }
}

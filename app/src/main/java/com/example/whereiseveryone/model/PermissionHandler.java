package com.example.whereiseveryone.model;


import androidx.annotation.Nullable;

import java.util.List;

public interface PermissionHandler {
    @Nullable
    List<String> arePermissionGranted(List<String> required);

    // Just trigger AndroidDialog for permissions
    boolean askForPermission(String permission);
}

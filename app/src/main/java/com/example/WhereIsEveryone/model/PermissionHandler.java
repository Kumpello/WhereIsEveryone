package com.example.WhereIsEveryone.model;


import androidx.annotation.Nullable;

import java.util.List;

public interface PermissionHandler {
    // TODO(kumpel): No message, it's not up to the PermissionHandler
    //               to show UI elements!
    void askForPermission(String permission, String message);


    // TODO(kumpel): I think this interface should be more like:
    interface V2 {
        // Returns not-granted permission;
        // If all are granted return null;
        // Why List? Normally we ask for multiple-permission (like Location+FineLocation+Camera).
        @Nullable
        List<String> arePermissionGranted(List<String> required);

        // Just trigger AndroidDialog for permissions
        void askForPermission(String permission);
    }
}

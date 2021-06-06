package com.example.WhereIsEveryone;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import android.content.res.Resources;

public class PermissionHandlerImpl implements PermissionHandler {

    Resources resources;
    Activity activity;

    public PermissionHandlerImpl(Activity activity) {
        this.activity = activity;
        resources = activity.getResources();
    }

    @Override
    public void askForPermission(String permission, String message) {
        int requestCode = 2137;
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else if (shouldShowRequestPermissionRationale(activity, permission)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            builder.setTitle(resources.getString(R.string.permission_needed));
            builder.setMessage(message);
            builder.setNegativeButton(resources.getString(R.string.decline), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});
            builder.setPositiveButton(resources.getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(activity, new String[]{permission}, requestCode);
                    dialog.dismiss();
                }});
            builder.show();
        } else {
            requestPermissions(activity, new String[] {permission}, requestCode);
        }
    }
}

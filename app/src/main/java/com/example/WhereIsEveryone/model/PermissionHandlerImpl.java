package com.example.WhereIsEveryone.model;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;


import com.example.WhereIsEveryone.R;
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
        if (ContextCompat.checkSelfPermission(activity,
                                              permission) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else if (shouldShowRequestPermissionRationale(activity, permission)) {
            // TODO(kumpel): Nope!
            //               It's view responsibility to build and show the dialog
            //               So, if "arePermissionsGranted" returns there are not,
            //               We should do something like:
            /*
                    in presenter:
                        if (!permissionHandler.arePermissionsGranted(requiredPermissions)) {
                            view.showPermissionDialog()
                        } else {
                            // no need to ask for permission
                        }

                    in view there is a method showPermissionDialog():

                    public void showPermissionDialog() {
                        showDialog(onSuccess = () -> presenter.permissionDialogOk(),
                                   onCancel = () -> presenter.permissionDialogNotOk()
                        )
                    }
             */


            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
            builder.setTitle(resources.getString(R.string.permission_needed));
            builder.setMessage(message);
            builder.setNegativeButton(resources.getString(R.string.decline),
                                      (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(resources.getString(R.string.accept), (dialog, which) -> {
                requestPermissions(activity, new String[]{permission}, requestCode);
                dialog.dismiss();
            });
            builder.show();
        } else {
            requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
}

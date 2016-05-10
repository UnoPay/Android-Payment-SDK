package com.techjini.android.paymnetlibrary.network.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.activities.SDKBaseActivity;
import com.techjini.android.paymnetlibrary.constants.UPConstants;


/**
 * Created by Nitin S.Mesta on 15/2/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class PermissionChecker {

    public static final int GRANT = 1;
    private static final int CANCEL = 2;

    public interface PermissionActionListener {
        public void onPermissionActionTaken(int requestCode, int action);
    }

    public static boolean checkIfPermissionGranted(Context context, String[] permissions, final int requestCode, boolean requestUser, final PermissionActionListener permissionActionListener) {

        boolean showCustomDialog = false;
        boolean isAllPermissionGranted = true;
        String permissionDescription = "";
        for (int permissionIndex = 0; permissionIndex < permissions.length; permissionIndex++) {
            if (ContextCompat.checkSelfPermission(context,
                    permissions[permissionIndex])
                    != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionGranted = false;
                if (requestUser) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[permissionIndex])) {
                        {
                            showCustomDialog = true;
                            int localRequestCode = permissions[permissionIndex] == Manifest.permission.READ_SMS ? UPConstants.SMS_PERMISSION_REQUEST_CODE : permissions[permissionIndex] == Manifest.permission.READ_PHONE_STATE ? UPConstants.READ_PHONE_STATUS_PERMISSION_REQUEST_CODE : UPConstants.COARSE_LOCATION_PERMISSION_REQUEST_CODE;
                            permissionDescription += noSufficientPermission(context, localRequestCode) + "\n\n";
                        }
                    } else {

                        ActivityCompat.requestPermissions((Activity) context,
                                permissions,
                                requestCode);
                        //Come out of loop as we are going to show all the permission request
                        break;

                    }
                } else {
                    return false;
                }

            }
        }
        if (showCustomDialog) {
            ((SDKBaseActivity) context).showDialog(context.getString(R.string.permission_required), permissionDescription, context.getString(R.string.button_ok), context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (permissionActionListener != null)
                        permissionActionListener.onPermissionActionTaken(requestCode, GRANT);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    permissionActionListener.onPermissionActionTaken(requestCode, CANCEL);
                }
            });
        }
        return isAllPermissionGranted;
    }


    private static String noSufficientPermission(final Context context, final int requestCode) {

        switch (requestCode) {
            case UPConstants.SMS_PERMISSION_REQUEST_CODE:
                return context.getString(R.string.sms_permission_not_available);

            case UPConstants.READ_PHONE_STATUS_PERMISSION_REQUEST_CODE:
                return context.getString(R.string.read_phone_state_permission_not_available);

            case UPConstants.COARSE_LOCATION_PERMISSION_REQUEST_CODE:
                return context.getString(R.string.location_permission_not_available);
        }
        return "Please enable required permission";
    }
}

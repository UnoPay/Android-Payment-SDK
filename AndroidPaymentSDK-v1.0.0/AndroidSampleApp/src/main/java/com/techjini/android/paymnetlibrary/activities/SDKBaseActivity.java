package com.techjini.android.paymnetlibrary.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.Utility.LocationHandler;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.constants.UPConstants;
import com.techjini.android.paymnetlibrary.network.model.PermissionChecker;

public class SDKBaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected TextView mToolBarTitle;
    protected ImageView mActionBack;
    protected AlertDialog mDialog;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
            mActionBack = (ImageView) findViewById(R.id.action_back);
            mActionBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }



    public void showDialog(String title, String message, String okText, String cancelText, Dialog.OnClickListener positiveOnClickListener, Dialog.OnClickListener negativeOnClickListener) {

        mDialog = new AlertDialog.Builder(this).create();
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.setCancelable(false);

        if (okText != null && positiveOnClickListener != null) {
            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, positiveOnClickListener);
        }

        if (cancelText != null && negativeOnClickListener != null) {
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelText, negativeOnClickListener);
        }

        if (!isFinishing()) {
            mDialog.show();
        }

    }


}

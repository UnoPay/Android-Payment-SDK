package com.techjini.android.paymnetlibrary.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.UnoPayParams;
import com.techjini.android.paymnetlibrary.Utility.LocationHandler;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.Wallet;
import com.techjini.android.paymnetlibrary.constants.UPConstants;
import com.techjini.android.paymnetlibrary.constants.UPErrorCodes;
import com.techjini.android.paymnetlibrary.constants.UPTransactionStatus;
import com.techjini.android.paymnetlibrary.fragments.VerifyOTP;
import com.techjini.android.paymnetlibrary.fragments.WalletList;
import com.techjini.android.paymnetlibrary.fragments.WalletSignIn;
import com.techjini.android.paymnetlibrary.network.model.PermissionChecker;
import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;
import com.techjini.android.paymnetlibrary.network.model.response.TransactionResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bheema on 20/01/16.
 * Company Techjini
 */
public class UnoPayPayment extends SDKBaseActivity implements WalletList.OnWalletInteraction, WalletSignIn.OnWalletSigInListener, VerifyOTP.OnVerifyOTPInteractionListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static int UNOPAY_PAYMENT_REQUEST_CODE = 110;
    public static String PAYMENT_PARAMS = "PaymentParams";
    private TextView mUnoPayBenefits;
    private LinearLayout mPoweredByLayout;
    private UnoPayParams mPaymentParams;
    private Fragment mCurrentFragment;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int REQUEST_ENABLE_LOCATION = 300;
    private LocationHandler mLocationHandler;

    PermissionChecker.PermissionActionListener mPermissionActionListener = new PermissionChecker.PermissionActionListener() {
        @Override
        public void onPermissionActionTaken(int requestCode, int action) {

            //TODO grant all the permission requested
            if (action == PermissionChecker.GRANT) {
                Utils.launchSettingAppWithResult(UnoPayPayment.this, UPConstants.PERMISSION_REQUEST_CODE);
            } else {
                initiatePlayServiceCheck();
            }
        }
    };
    private boolean isServerInteraction = false;
    private TransactionResponse mTransactionResponse = null;
    private boolean isTransactionSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);
        mUnoPayBenefits = (TextView) findViewById(R.id.unopay_benefits);
        mPoweredByLayout = (LinearLayout) findViewById(R.id.powered_by_layout);
        mPaymentParams = (UnoPayParams) getIntent().getSerializableExtra(PAYMENT_PARAMS);
        if (mPaymentParams != null) {

            if (isEmpty(mPaymentParams.getOrderId())) {
                notifyToRequester(getErrorIntent(getString(R.string.apikey_order_null), UPErrorCodes.INVALID_API_KEY_ORDER_ID.ordinal()));
                return;
            }
            if (isEmpty(mPaymentParams.getAppName())) {
                notifyToRequester(getErrorIntent(getString(R.string.please_provide_app_name), UPErrorCodes.APP_NAME_NOT_SET.ordinal()));
                return;
            }
            if (mPaymentParams.getAmount() <= 0) {
                notifyToRequester(getErrorIntent(getString(R.string.please_give_amount), UPErrorCodes.AMOUNT_NOT_SET.ordinal()));
                return;
            }

            if (isEmpty(mPaymentParams.getMerchantSdkKey())) {
                notifyToRequester(getErrorIntent(getString(R.string.please_give_merchant_sdk_key), UPErrorCodes.SDK_KEY_NOT_SET.ordinal()));
                return;
            }
            if (isEmpty(mPaymentParams.getPartnerId())) {
                notifyToRequester(getErrorIntent(getString(R.string.please_give_partner_id), UPErrorCodes.PARTNER_ID_NOT_SET.ordinal()));
                return;
            }
            mToolBarTitle.setText(getString(R.string.rupee_symbol) + " " + Utils.formatAmount(mPaymentParams.getAmount()));
            mUnoPayBenefits.setMovementMethod(LinkMovementMethod.getInstance());
            mLocationHandler = new LocationHandler(UnoPayPayment.this);

            if (PermissionChecker.checkIfPermissionGranted(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, UPConstants.PERMISSION_REQUEST_CODE, true, mPermissionActionListener)) {
                initiatePlayServiceCheck();

            }


        } else {
            notifyToRequester(getErrorIntent(getString(R.string.please_provide_payment_params), UPErrorCodes.PARAMS_NOT_SET.ordinal()));
            return;
        }
    }

    private GoogleApiClient mGoogleClient;

    private void startLocationPolling() {

        mGoogleClient = new GoogleApiClient.Builder(UnoPayPayment.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleClient.connect();

    }

    private void initiatePlayServiceCheck() {
        if (checkPlayServices()) {
            initiateLocationChecks();
        }
    }


    protected boolean checkPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            final GPSResultAction gpsResultAction = getGPSErrorMessageAndActionFromCode(resultCode);

            showDialog(getString(R.string.google_play_error), gpsResultAction.message, gpsResultAction.positiveButtonName, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (gpsResultAction.action == GPSResultAction.ACTION_LAUNCH_PLAY_STORE) {
                        Utils.launchPlayStore(UnoPayPayment.this, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
                        finish();
                    } else if (gpsResultAction.action == GPSResultAction.ACTION_PROCEED) {

                        initiatePayment();
                    } else if (gpsResultAction.action == GPSResultAction.ACTION_LAUNCH_PLAY_SERVICE_SETTING) {
                        Utils.launchAppSettings(UnoPayPayment.this, GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
                        finish();
                    } else if (gpsResultAction.action == GPSResultAction.ACTION_FINISH) {
                        finish();
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            return false;
        }
        return true;
    }

    //Google Play Service
    private GPSResultAction getGPSErrorMessageAndActionFromCode(int resultCode) {
        GPSResultAction gpsResultAction = new GPSResultAction();
        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {


            switch (resultCode) {
                case 1:
                    gpsResultAction.message = getString(R.string.google_play_service_required);
                    gpsResultAction.action = GPSResultAction.ACTION_LAUNCH_PLAY_STORE;
                    gpsResultAction.positiveButtonName = "Install";

                    break;
                case 2:
                    gpsResultAction.message = getString(R.string.google_play_service_update_required);
                    gpsResultAction.action = GPSResultAction.ACTION_LAUNCH_PLAY_STORE;
                    gpsResultAction.positiveButtonName = "Update";
                    break;

                case 3:
                    gpsResultAction.message = getString(R.string.enable_google_play_service);
                    gpsResultAction.action = GPSResultAction.ACTION_LAUNCH_PLAY_SERVICE_SETTING;
                    gpsResultAction.positiveButtonName = "Enable";
                    break;

                case 5:
                    // optional- allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_service_invalid_account);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 7:
                    // optional- allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_service_data_connection_required);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 9:
                    // Should not allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_service_not_supported);
                    gpsResultAction.action = GPSResultAction.ACTION_FINISH;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 16:
                    //optional- allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_api_unavailable);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 17:
                    //optional- allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_service_error_signin);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 18:
                    // allow to proceed
                    gpsResultAction.message = getString(R.string.google_play_service_updating);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "OK";
                    break;

                case 42:
                    gpsResultAction.message = getString(R.string.google_play_service_update_android_wear_app);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "Proceed";
                    break;

                default:
                    gpsResultAction.message = getString(R.string.common_google_play_services_unknown_issue);
                    gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
                    gpsResultAction.positiveButtonName = "Proceed";

                    break;

            }
        } else {
            gpsResultAction.message = getString(R.string.common_google_play_services_unknown_issue);
            gpsResultAction.action = GPSResultAction.ACTION_PROCEED;
            gpsResultAction.positiveButtonName = "Proceed";
        }
        return gpsResultAction;

    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = mLocationHandler.getmLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        builder.setNeedBle(false);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        mLocationHandler.start();
                        initiatePayment();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    UnoPayPayment.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        initiatePayment();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        initiatePayment();
    }

    private class GPSResultAction {
        public static final int ACTION_FINISH = 1;
        public static final int ACTION_PROCEED = 2;
        public static final int ACTION_LAUNCH_PLAY_STORE = 3;
        public static final int ACTION_LAUNCH_PLAY_SERVICE_SETTING = 4;
        public String message;
        public String positiveButtonName;
        public int action;
    }


    private void initiateLocationChecks() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startLocationPolling();
        } else {
            if (PermissionChecker.checkIfPermissionGranted(UnoPayPayment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, UPConstants.PERMISSION_REQUEST_CODE, true, mPermissionActionListener)) {
                startLocationPolling();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initiatePayment();
    }

    private boolean isEmpty(String mOrderId) {
        if (mOrderId != null && mOrderId.trim().length() > 0) {
            return false;
        }
        return true;
    }


    public void initiatePayment() {
        if (!Utils.isUnoPaySupportedVersion()) {
            if (Utils.isUnoPayInstalled(this, mPaymentParams.isProduction())) {

                if (Utils.isUnoPaySupportedVersionInstalled(this, mPaymentParams.isProduction())) {
                    try {
                        Intent intent = new Intent(getString(R.string.action_payment));
                        intent.putExtra(PAYMENT_PARAMS, mPaymentParams.toString());
                        startActivityForResult(intent, UNOPAY_PAYMENT_REQUEST_CODE);
                        //Processing the request
                    } catch (ActivityNotFoundException e) {
                        //TODO go with wallet payment
                        addFragment(WalletList.newInstance(mPaymentParams));
                    }
                } else {
                    showDialog(getString(R.string.label_error), getString(R.string.unopay_older_version), "Update", "Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utils.launchPlayStore(UnoPayPayment.this, Utils.getUnopayPackageName(mPaymentParams.isProduction()));
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO go with wallet payment
                            dialog.dismiss();
                            addFragment(WalletList.newInstance(mPaymentParams));
                        }
                    });
                }

            } else {

                addFragment(WalletList.newInstance(mPaymentParams));
            }

        } else {


            notifyToRequester(getErrorIntent(getString(R.string.notsupported), UPErrorCodes.API_NOT_SUPPORTED.ordinal()));
            Toast.makeText(this, getString(R.string.notsupported), Toast.LENGTH_SHORT).show();
        }


    }


    private void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        mCurrentFragment = fragment;
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
        showPoweredBy();

    }

    private void showPoweredBy() {
        if (mCurrentFragment instanceof VerifyOTP) {
            mPoweredByLayout.setVisibility(View.GONE);

        } else if (mCurrentFragment instanceof WalletSignIn) {
            mUnoPayBenefits.setVisibility(View.GONE);
            mPoweredByLayout.setVisibility(View.VISIBLE);
        } else if (mCurrentFragment instanceof WalletList) {
            mPoweredByLayout.setVisibility(View.VISIBLE);
            mUnoPayBenefits.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {

        if (!isServerInteraction) {
            if (!isTransactionSuccessful) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    showDialog("Transaction", "Do you want to cancel this transaction?", "Ok", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            notifyToRequester(getErrorIntent(getString(R.string.user_cancelled_transaction), UPErrorCodes.USER_CANCELLED_TRANSACTION.ordinal()));
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    return;
                } else {
                    super.onBackPressed();
                    if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
                        finish();
                    } else {
                        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                        mCurrentFragment = getSupportFragmentManager().findFragmentByTag(tag);
                        showPoweredBy();
                    }
                }
            }

        }

    }

    private Intent getErrorIntent(String message, int errorCode) {
        Intent intent = new Intent();
        intent.putExtra("RESULT", getErrorStatusInJson(message, errorCode));
        intent.putExtra("ORDER_ID", mPaymentParams.getOrderId());
        return intent;
    }

    private Intent getSuccessIntent(TransactionResponse transactionResponse) {
        Intent successIntent = new Intent();
        successIntent.putExtra("RESULT", getSuccessDatInJson(transactionResponse));
        successIntent.putExtra("ORDER_ID", mPaymentParams.getOrderId());
        return successIntent;
    }

    private Intent getSuccessIntent(TransactionResponse.Data transactionResponse) {
        Intent successIntent = new Intent();
        successIntent.putExtra("RESULT", getSuccessDatInJson(transactionResponse));
        successIntent.putExtra("ORDER_ID", mPaymentParams.getOrderId());
        return successIntent;
    }


    private String getErrorStatusInJson(String message, int errorCode) {
        JSONObject statusObject = new JSONObject();
        try {
            statusObject.put("status", errorCode == UPErrorCodes.USER_CANCELLED_TRANSACTION.ordinal() ? UPTransactionStatus.CANCELLED.ordinal() : UPTransactionStatus.FAILED.ordinal());
            statusObject.put("data", null);
            statusObject.put("error", getErrorBlock(message, errorCode));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusObject.toString();
    }

    private String getSuccessDatInJson(TransactionResponse transactionResponse) {
        JSONObject statusObject = new JSONObject();
        try {
            statusObject.put("status", UPTransactionStatus.SUCCESS.ordinal());
            statusObject.put("data", transactionResponse.getResultJSON());
            statusObject.put("error", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusObject.toString();
    }

    private String getSuccessDatInJson(TransactionResponse.Data transactionResponse) {
        JSONObject statusObject = new JSONObject();
        try {
            statusObject.put("status", UPTransactionStatus.SUCCESS.ordinal());
            statusObject.put("data", transactionResponse.getResultJSON());
            statusObject.put("error", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusObject.toString();
    }


    private JSONObject getErrorBlock(String message, int code) {
        JSONObject errorObject = new JSONObject();
        try {
            errorObject.put("message", message);
            errorObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorObject;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNOPAY_PAYMENT_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                //Handle Response sent by app and notify
                if (data.hasExtra("RESULT")) {
                    String result = data.getStringExtra("RESULT");
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject != null) {
                            String status = jsonObject.getString("status");
                            if (status != null && status.equalsIgnoreCase("success")) {
                                notifyToRequester(getSuccessIntent(new Gson().fromJson(result, TransactionResponse.Data.class)));
                            } else if (status != null && status.equalsIgnoreCase("failure")) {
                                String message = jsonObject.getString("message");
                                notifyToRequester(getErrorIntent(message, UPErrorCodes.TRANSACTION_FAILED.ordinal()));
                            } else {
                                notifyToRequester(getErrorIntent(getString(R.string.transaction_failure), UPErrorCodes.TRANSACTION_FAILED.ordinal()));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        notifyToRequester(getErrorIntent(getString(R.string.transaction_failure), UPErrorCodes.TRANSACTION_FAILED.ordinal()));
                    }

                } else {
                    notifyToRequester(getErrorIntent(getString(R.string.transaction_failure), UPErrorCodes.TRANSACTION_FAILED.ordinal()));
                }

            } else {
                notifyToRequester(getErrorIntent(getString(R.string.transaction_failure), UPErrorCodes.TRANSACTION_FAILED.ordinal()));
            }
        }

        if (requestCode == UPConstants.PERMISSION_REQUEST_CODE) {
            initiatePayment();
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All
                    startLocationPolling();

                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    initiatePayment();
                    break;
                default:
                    break;
            }
            if (requestCode == REQUEST_ENABLE_LOCATION) {

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All
                        startLocationPolling();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        initiatePayment();
                        break;
                    default:
                        break;
                }

            }
        }
    }

    private void notifyToRequester(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onWalletSelected(Wallet wallet) {
        WalletSignIn walletSignIn = WalletSignIn.newInstance(wallet, String.valueOf(mPaymentParams.getMobileNumber()), mPaymentParams);
        addFragment(walletSignIn);
    }

    @Override
    public void isServerTransaction(boolean isTransactionHappening) {
        isServerInteraction = isTransactionHappening;
    }


    @Override
    public void onOTPSuccess(OTPRequestParams otpRequestParams) {
        addFragment(VerifyOTP.newInstance(otpRequestParams, mPaymentParams));
    }


    @Override
    public void onTransactionComplete(TransactionResponse transactionResponse) {
        isTransactionSuccessful = true;
        notifyToRequester(getSuccessIntent(transactionResponse));
    }
}

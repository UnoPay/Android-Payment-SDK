package com.techjini.android.paymnetlibrary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.MalformedJsonException;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.techjini.android.paymnetlibrary.activities.SDKBaseActivity;
import com.techjini.android.paymnetlibrary.constants.UPConstants;
import com.techjini.android.paymnetlibrary.network.model.Device;
import com.techjini.android.paymnetlibrary.network.model.Location;
import com.techjini.android.paymnetlibrary.network.model.PermissionChecker;
import com.techjini.android.paymnetlibrary.network.model.Platform;
import com.techjini.android.paymnetlibrary.network.model.Validation;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class Utils {

    private static final String LAST_KNOWN_LOCATION = "last_known_location";
    private static final String SDK_PREFERENCE = "SDKPreference";

    public static boolean isUnoPaySupportedVersion() {

        if (Build.VERSION.SDK_INT < 15) {
            return true;
        }
        return false;
    }

    public static void launchSettingAppWithResult(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        ((SDKBaseActivity) context).startActivityForResult(intent, requestCode);
    }

    public static String getNetTypeAsString(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return "unknown";
        }
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return "WIFI";
        }
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return "Cellular";
        }
        return "unknown";
    }

    public static String getNetSubTypeAsString(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return "unknown";
        }
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int sub = info.getSubtype();
            switch (sub) {

                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2g";

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3g";

                case TelephonyManager.NETWORK_TYPE_LTE:

                    return "4g";

                case TelephonyManager.NETWORK_TYPE_UNKNOWN:

                    return "unknown";

                default:
                    return "unknown";
            }
        }
        return "unknown";
    }

    public static boolean isNfcAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getNetworkServiceProvider(Context context) {
        String operatorName = null;
        TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

        operatorName = telephonyManager.getSimOperatorName();
        if (operatorName == null) {
            operatorName = telephonyManager.getNetworkOperatorName();
        }
        return operatorName;
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getDeviceId(Context context) {

        String id = null;


        if(PermissionChecker.checkIfPermissionGranted(context, new String[]{Manifest.permission.READ_PHONE_STATE}, UPConstants.READ_PHONE_STATUS_PERMISSION_REQUEST_CODE, false, null)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                id = tm.getDeviceId();
            }
            if (id == null || isAllZero(id)) {
                id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }else {
            if (id == null || isAllZero(id)) {
                id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return id;
    }

    private static boolean isAllZero(String id) {
        String allZeroPattern = "^0*$";
        Pattern pattern = Pattern.compile(allZeroPattern);
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static int getDpToPixels(Context context, int dp) {
        if (context != null) {
            Resources r = context.getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());

            return px;
        }
        return dp;
    }

    public static Drawable getSupportDrawable(Context context, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId, null);
        }
    }

    public static boolean isUnoPayInstalled(Context context,boolean isProduction) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(isProduction?"in.unopay.android.buyer.prod":"in.unopay.android.buyer.dev", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void launchPlayStore(Context context,String packageName) {
        final String appPackageName = packageName; // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
    public static void launchAppSettings(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        context.startActivity(intent);

    }

    public static boolean isUnoPaySupportedVersionInstalled(Context context,boolean isProduction)
    {
        PackageManager pm=context.getPackageManager();
        try {
            PackageInfo packageInfo=pm.getPackageInfo(isProduction?"in.unopay.android.buyer.prod":"in.unopay.android.buyer.dev",PackageManager.GET_ACTIVITIES);
            if(packageInfo.versionCode<13)
            {
                return false;
            }else {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Optional link more using Apache Utils http://stackoverflow.com/questions/4895523/java-string-to-sha1
    public static String getSha1Value(String text) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = text.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static void storeLocation(Context context,Location location)
    {
       SharedPreferences sharedPreferences=context.getSharedPreferences(SDK_PREFERENCE,Context.MODE_PRIVATE);
       SharedPreferences.Editor editor=sharedPreferences.edit();
       editor.putString(LAST_KNOWN_LOCATION,location.toString());
       editor.commit();
    }

    public static Location getLastKnownLocation(Context context)
    {
        Location lastKnownLocation=null;
        String locationString=context.getSharedPreferences(SDK_PREFERENCE, Context.MODE_PRIVATE).getString(LAST_KNOWN_LOCATION,null);
        if(locationString!=null)
        {
           lastKnownLocation= new Gson().fromJson(locationString,Location.class);
        }

        return lastKnownLocation;
    }


    public static String getServerErrorMessage(int responseCode, UnoPayResponse response, Context context, String defaultMessage) {

        String message = defaultMessage;

        if (responseCode == 503) {

            message = context.getResources().getString(R.string.unopay_five_not_three_error);
        } else if (("" + responseCode).startsWith("5")) {
            message = context.getResources().getString(R.string.unopay_five_x_error);
        } else {
            if (response != null && response.getError()!=null) {
                message = response.getError().getMessage();

            }
            if (TextUtils.isEmpty(message)) {
                message = defaultMessage;
            }

        }

        return message;

    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();//0123456789ABCDEF
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getFailureError(Throwable t, Context context) {


        if (context != null) {
            if (t instanceof SocketTimeoutException) {
                return context.getString(R.string.connection_timeout);

            } else if (t instanceof TimeoutException) {
                return context.getString(R.string.connection_timeout);
            } else {
                return context.getString(R.string.default_network_error_message);
            }
        } else {
            return "Oops! Something went wrong.Please try again later.";
        }
    }


    public static String getAppTransactionId(String merchantSDKkey) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMDDHHmmss");

        Random random = new Random();
        int rand = (random.nextInt(1000000)) % 999999;

        return  merchantSDKkey + "-" + format.format(new
                Date()) + "-" +
                rand;
    }


    public static Validation isValidMobileNumber(String mobileNumber) {
        Validation validation=new Validation();
        if(mobileNumber.trim().length()<10)
        {
            validation.mIsValid=false;
            validation.mMessage="Please enter the valid mMobile number";
        }else {
            validation.mIsValid=true;
            validation.mMessage=null;
        }
        return validation;
    }

    public static Validation isValidOtp(String otp) {
        Validation validation=new Validation();
        if(otp.trim().length()<1)
        {
            validation.mIsValid=false;
            validation.mMessage="Please enter the OTP";
        }else {
            validation.mIsValid=true;
            validation.mMessage=null;
        }
        return validation;
    }

    public static String formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(amount);
    }

    public static Device populateDeviceInfo(Context context) {
        Device mDeviceInfo = new Device();
        mDeviceInfo.setId(Utils.getDeviceId(context));
        mDeviceInfo.setAppType("buyer");
        mDeviceInfo.setAppVersion(Utils.getAppVersion(context));
        Platform platform = new Platform();
        platform.setModel(Utils.getDeviceName());
        platform.setOs("android");
        platform.setOsVersion(Build.VERSION.RELEASE);
        platform.setServiceProvider(Utils.getNetworkServiceProvider(context));
        platform.setNfcSupport(Utils.isNfcAvailable(context));
        platform.setNetworkType(Utils.getNetTypeAsString(context));
        platform.setCellularType(Utils.getNetSubTypeAsString(context));
        mDeviceInfo.setPlatform(platform);

        //TODO change the hard coded location object
        Location location=getLastKnownLocation(context);
        if(location==null) {
            location=new Location();
            location.setLatitude(0.0);
            location.setLongitude(0.0);
            location.setAccuracy(0.0f);
            location.setProvider("NA");
            location.setTime(System.currentTimeMillis());
         }

        mDeviceInfo.setLocation(location);

        return mDeviceInfo;
    }

    public static String getUnopayPackageName(boolean production) {
        if(production)
        {
            return "in.unopay.android.buyer.prod";
        }else {
            return "in.unopay.android.buyer.dev";
        }

    }
}

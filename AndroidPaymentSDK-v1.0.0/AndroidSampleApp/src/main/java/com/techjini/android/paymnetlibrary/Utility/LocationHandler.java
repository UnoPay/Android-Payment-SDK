package com.techjini.android.paymnetlibrary.Utility;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.techjini.android.paymnetlibrary.Utils;

/**
 * Created by Nitin S.Mesta on 9/5/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */

public class LocationHandler implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    private LocationRequest mLocationRequest;
    private final int LOCATION_ACCURACY = 2000; //2000 meters
    // Location updates intervals in sec
    private final int UPDATE_INTERVAL = 5000; // 5 sec
    private final int FATEST_INTERVAL = 2000; // 2 sec
    private final int DISPLACEMENT = 1; // 1 meters

    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }

    public void setmLocationRequest(LocationRequest mLocationRequest) {
        this.mLocationRequest = mLocationRequest;
    }

    private GoogleApiClient mGoogleApiClient;

    public LocationHandler(Context context) {
        this.mContext=context;
        createLocationRequest();
        buildGoogleApiClient(this, this);
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && location.getAccuracy() < LOCATION_ACCURACY) {
            com.techjini.android.paymnetlibrary.network.model.Location localLocation=new com.techjini.android.paymnetlibrary.network.model.Location();
            localLocation.setTime(location.getTime());
            localLocation.setAccuracy(location.getAccuracy());
            localLocation.setLatitude(location.getLatitude());
            localLocation.setLongitude(location.getLongitude());
            localLocation.setProvider(location.getProvider());
            Utils.storeLocation(mContext,localLocation);
            stop();
        }
    }



    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            com.techjini.android.paymnetlibrary.network.model.Location localLocation=new com.techjini.android.paymnetlibrary.network.model.Location();
            localLocation.setTime(mLastLocation.getTime());
            localLocation.setAccuracy(mLastLocation.getAccuracy());
            localLocation.setLatitude(mLastLocation.getLatitude());
            localLocation.setLongitude(mLastLocation.getLongitude());
            localLocation.setProvider(mLastLocation.getProvider());
            Utils.storeLocation(mContext,localLocation);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    public void stop() {
        stopLocationUpdates();

    }
}

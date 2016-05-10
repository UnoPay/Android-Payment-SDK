package com.techjini.android.paymnetlibrary.network.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Bheema on 18/09/15.
 */
public class Location implements Serializable {
    private double latitude;
    private double longitude;
    private long time;
    private String provider;
    private float accuracy;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

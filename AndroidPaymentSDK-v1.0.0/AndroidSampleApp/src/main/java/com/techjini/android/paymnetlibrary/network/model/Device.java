package com.techjini.android.paymnetlibrary.network.model;

import java.io.Serializable;

/**
 * Created by Bheema on 09/10/15.
 * <p/>
 * Company Techjini
 */
public class Device implements Serializable{
    private String id;
    private long time;
    private Location location;
    private String appType;
    private int appVersion;
    private Platform platform;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

}

package com.techjini.android.paymnetlibrary.network.model.request;


import com.google.gson.annotations.SerializedName;
import com.techjini.android.paymnetlibrary.network.model.Device;

import java.io.Serializable;

/**
 * Created by bheema on 14/09/15.
 */
public abstract class UnoPayRequest implements Serializable {
    @SerializedName("device")
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }


    abstract public String getJsonString();

}

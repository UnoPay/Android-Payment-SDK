package com.techjini.android.paymnetlibrary.network.model.request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.techjini.android.paymnetlibrary.Utils;

import java.io.Serializable;

/**
 * Created by Nitin S.Mesta on 29/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class VerifyOTPRequest extends UnoPayRequest implements Serializable{
    @SerializedName("data")
    public Data data;

    public VerifyOTPRequest(Context context)
    {
        data=new Data();
        setDevice(Utils.populateDeviceInfo(context));
    }

    public class Data implements Serializable{
        @SerializedName("mobile")
        public String mobile;
        @SerializedName("otp")
        public String otp;
        @SerializedName("partnerId")
        public String partnerId;
        @SerializedName("sdkApiKey")
        public String sdkApiKey;
        @SerializedName("transactionId")
        public String transactionId;
        @SerializedName("walletId")
        public String walletId;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
    @Override
    public String getJsonString() {
        return new Gson().toJson(this);
    }
}

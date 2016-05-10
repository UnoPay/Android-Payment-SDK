package com.techjini.android.paymnetlibrary.network.model.request;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.network.model.Device;
import com.techjini.android.paymnetlibrary.network.model.Location;
import com.techjini.android.paymnetlibrary.network.model.Platform;

import java.io.Serializable;

import okhttp3.internal.Util;

/**
 * Created by Nitin S.Mesta on 29/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class OTPRequestParams extends UnoPayRequest implements Serializable {

    @SerializedName("data")
    public Data mData;

    public OTPRequestParams(Context context)
    {
        setDevice(Utils.populateDeviceInfo(context));
        mData=new Data();
    }



    public class Data implements Serializable {
        @SerializedName("amount")
        public String mAmount;
        @SerializedName("appTransactionId")
        public String mAppTransactionId;
        @SerializedName("mobile")
        public String mMobile;
        @SerializedName("orderId")
        public String mOrderId;
        @SerializedName("partnerId")
        public String mPartnerId;
        @SerializedName("sdkApiKey")
        public String mSdkApiKey;
        @SerializedName("transactionId")
        public String mTransactionId;
        @SerializedName("walletId")
        public String mWalletId;



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

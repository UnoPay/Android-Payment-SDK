package com.techjini.android.paymnetlibrary.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Bheema on 02/03/16.
 * Company Techjini
 */
public class PushNotification implements Serializable {
    @SerializedName("type")
    public String type;
    @SerializedName("isShowNotification")
    public boolean isShowNotification;
    @SerializedName("alert")
    public Alert alert;
    @SerializedName("parameters")
    public Parameters parameters;


    public class Alert implements Serializable {
        @SerializedName("title")
        public String title;
        @SerializedName("body")
        public String body;

    }

    public class Parameters implements Serializable {
        @SerializedName("status")
        public String status;
        @SerializedName("amount")
        public double amount;
        @SerializedName("merchantName")
        public String merchantName;
        @SerializedName("wallet")
        public String wallet;
        @SerializedName("message")
        public String message;
        @SerializedName("discount")
        public String discountStr;
        @SerializedName("discountValue")
        public double discountValue;
        @SerializedName("otpFrom")
        public String otpFrom;
        @SerializedName("paymentRequestId")
        public String paymentRequestId;

    }
}

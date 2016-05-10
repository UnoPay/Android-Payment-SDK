package com.techjini.android.paymnetlibrary;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Bheema on 13/04/16.
 * Company Techjini
 */
public class UnoPayParams implements Serializable{
    private String orderId;
    private double amount;
    private String merchantSdkKey;
    private String partnerId;
    private long mobileNumber;
    private String email;
    private String name;
    private String appName;
    private boolean isProduction=false;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchantSdkKey() {
        return merchantSdkKey;
    }

    public void setMerchantSdkKey(String merchantSdkKey) {
        this.merchantSdkKey = merchantSdkKey;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isProduction() {
        return isProduction;
    }

    public void setProduction(boolean production) {
        isProduction = production;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

package com.techjini.android.paymnetlibrary;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Bheema on 18/04/16.
 * Company Techjini
 */
public class Wallet implements Serializable{
    private String id;
    private String name;
    private String logoUrl;
    @SerializedName("paymentLimit")
    private LoadMoneyParams loadMoneyLimit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public LoadMoneyParams getLoadMoneyLimit() {
        return loadMoneyLimit;
    }

    public void setLoadMoneyLimit(LoadMoneyParams loadMoneyLimit) {
        this.loadMoneyLimit = loadMoneyLimit;
    }


    public class LoadMoneyParams implements Serializable {
        private int min;
        private int max;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

}

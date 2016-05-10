package com.techjini.android.paymnetlibrary.network.model;

import java.io.Serializable;

/**
 * Created by Bheema on 09/10/15.
 * Company Techjini
 */
public class Platform implements Serializable{
    private String os;
    private String osVersion;
    private String model;
    private String serviceProvider;
    private boolean nfcSupport;
    private String networkType;
    private String cellularType;


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }


    public boolean isNfcSupport() {
        return nfcSupport;
    }

    public void setNfcSupport(boolean nfcSupport) {
        this.nfcSupport = nfcSupport;
    }

    public String getCellularType() {
        return cellularType;
    }

    public void setCellularType(String cellularType) {
        this.cellularType = cellularType;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}

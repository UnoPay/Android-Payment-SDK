package com.techjini.android.paymnetlibrary.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class UnoPayResponse implements Serializable {
    @SerializedName("code")
    public int code;
    @SerializedName("tag")
    public int tag;
    @SerializedName("message")
    public String message;
    @SerializedName("error")
    private UnoPayErrorResponse error;
    @SerializedName("meta")
    private MetaData meta;

    public UnoPayErrorResponse getError() {
        return error;
    }

    public void setError(UnoPayErrorResponse error) {
        this.error = error;
    }

    public MetaData getMeta() {
        return meta;
    }

    public void setMeta(MetaData meta) {
        this.meta = meta;
    }
}

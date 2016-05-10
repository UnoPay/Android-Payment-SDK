package com.techjini.android.paymnetlibrary.network.model.response;

import com.google.gson.annotations.SerializedName;
import com.techjini.android.paymnetlibrary.Wallet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class MasterWalletResponse extends UnoPayResponse implements Serializable{
    @SerializedName("data")
    public Data data;

    public class Data implements Serializable{
        @SerializedName("wallets")
        public ArrayList<Wallet> wallets;

    }

}

package com.techjini.android.paymnetlibrary.network.model.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Nitin S.Mesta on 29/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class TransactionResponse extends UnoPayResponse implements Serializable{

    @SerializedName("data")
    public Data data;

    public class Data implements Serializable{
        @SerializedName("message")
        public String mMessage;
        @SerializedName("transactionId")
        public String mTransactionId;
        @SerializedName("amount")
        public double mAmount;
        @SerializedName("requestedAmount")
        public double mRequestedAmount;
        @SerializedName("merchantAmount")
        public double mMerchantAmount;
        @SerializedName("discount")
        public String mDiscount;
        @SerializedName("merchantRefId")
        public String mMerchantRefId;
        @SerializedName("walletName")
        public String mWalletName;
        @SerializedName("discountBy")
        public double mDiscountBy;
        @SerializedName("walletId")
        public double mWalletId;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        public JSONObject getResultJSON()
        {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("message",mMessage);
                jsonObject.put("transactionId",mTransactionId);
                jsonObject.put("requestedAmount",mRequestedAmount);
                jsonObject.put("merchantAmount",mMerchantAmount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public JSONObject getResultJSON()
    {
        return data!=null?data.getResultJSON():null;
    }
}


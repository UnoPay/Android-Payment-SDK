package com.techjini.android.paymnetlibrary.interfaces;

import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;

/**
 * Created by Nitin S.Mesta on 2/5/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public interface VerifyOTPCallBack extends UnopayServerCallBack {
    public void onVerifyOTPSuccess(UnoPayResponse transactionResponse);
    public void onVerifyOTPFailure(UnoPayResponse transactionResponse);

}

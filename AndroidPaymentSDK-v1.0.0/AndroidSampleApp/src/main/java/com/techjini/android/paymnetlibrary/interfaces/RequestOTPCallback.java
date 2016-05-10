package com.techjini.android.paymnetlibrary.interfaces;

import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;

/**
 * Created by Nitin S.Mesta on 2/5/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public interface RequestOTPCallback extends UnopayServerCallBack {
    public void onOTPRequestSuccess(OTPRequestParams otpRequestParams);
    public void onOTPRequestFailed(OTPRequestParams otpRequestParams,String errorResponse);
}

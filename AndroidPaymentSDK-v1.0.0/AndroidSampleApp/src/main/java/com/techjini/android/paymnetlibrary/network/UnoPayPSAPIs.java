package com.techjini.android.paymnetlibrary.network;

import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;
import com.techjini.android.paymnetlibrary.network.model.request.VerifyOTPRequest;
import com.techjini.android.paymnetlibrary.network.model.response.MasterWalletResponse;
import com.techjini.android.paymnetlibrary.network.model.response.TransactionResponse;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public interface UnoPayPSAPIs {

    @POST("request-otp-transaction")
    Call<TransactionResponse> requestOTPForTransaction(@Header("Checksum") String checksum, @Header("partnerId") String partnerId, @Header("sdkApiKey") String sdkApiKey, @Body OTPRequestParams otpRequestParams);

    @POST("verify-otp-transaction")
    Call<TransactionResponse> verifyOTPForTransaction(@Header("Checksum") String checksum, @Header("partnerId") String partnerId, @Header("sdkApiKey") String sdkApiKey, @Body VerifyOTPRequest verifyOTPRequest);
}

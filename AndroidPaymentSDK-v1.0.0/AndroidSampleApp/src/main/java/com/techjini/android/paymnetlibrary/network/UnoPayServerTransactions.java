package com.techjini.android.paymnetlibrary.network;

import android.content.Context;

import com.google.gson.Gson;
import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.constants.NetworkConstants;
import com.techjini.android.paymnetlibrary.interfaces.GetWalletListCallBack;
import com.techjini.android.paymnetlibrary.interfaces.RequestOTPCallback;
import com.techjini.android.paymnetlibrary.interfaces.UnopayServerCallBack;
import com.techjini.android.paymnetlibrary.interfaces.VerifyOTPCallBack;
import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;
import com.techjini.android.paymnetlibrary.network.model.request.VerifyOTPRequest;
import com.techjini.android.paymnetlibrary.network.model.response.MasterWalletResponse;
import com.techjini.android.paymnetlibrary.network.model.response.TransactionResponse;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayErrorResponse;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nitin S.Mesta on 27/4/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class UnoPayServerTransactions {
    private Context mContext;
    private UnoPayBSAPIs mUPBSAPIs;
    private UnoPayPSAPIs mUPPSAPIs;
    private UnopayServerCallBack mUnoPayServerCallBack;


    public UnoPayServerTransactions(Context context, UnopayServerCallBack unopayServerCallBack, boolean isProduction) {
        mContext = context;
        mUnoPayServerCallBack = unopayServerCallBack;
        mUPBSAPIs = ServiceGenerator.createService(UnoPayBSAPIs.class, isProduction ? NetworkConstants.URL.BASE_URL : NetworkConstants.URL.DEV_BASE_URL, isProduction);
        mUPPSAPIs = ServiceGenerator.createService(UnoPayPSAPIs.class, isProduction ? NetworkConstants.URL.PS_URL : NetworkConstants.URL.DEV_PS_URL, isProduction);
    }

    public void getOTPEnabledWallets(String partnerId, String sdkAPIKey) {
        String checkSum = Utils.getSha1Value(sdkAPIKey + "otp-enabled-wallets" + sdkAPIKey);
        Call<MasterWalletResponse> getOTPEnabledWalletCall = mUPBSAPIs.getOTPEnabledWallets(checkSum, partnerId, sdkAPIKey);

        getOTPEnabledWalletCall.enqueue(new Callback<MasterWalletResponse>() {
            @Override
            public void onResponse(Call<MasterWalletResponse> call, Response<MasterWalletResponse> response) {

                if (mUnoPayServerCallBack != null) {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            MasterWalletResponse masterWalletResponse = response.body();
                            //TODO handle the list to pass the data to the caller
                            if (masterWalletResponse != null) {
                                ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListSuccess(masterWalletResponse.data.wallets, masterWalletResponse);
                            } else {

                                UnoPayResponse unoPayResponse = new UnoPayResponse();
                                UnoPayErrorResponse errorResponse = new UnoPayErrorResponse();
                                errorResponse.setMessage(mContext.getString(R.string.default_network_error_message));
                                unoPayResponse.setError(errorResponse);
                                ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListFailure(unoPayResponse.getError().getMessage());
                            }
                        } else {

                            Gson gson = new Gson();
                            UnoPayResponse unoPayResponse = null;

                            String error = null;
                            try {
                                error = response.errorBody().string();
                                if (error != null) {
                                    unoPayResponse =
                                            new Gson().fromJson(error, UnoPayResponse.class);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {

                            }


                            if (unoPayResponse != null) {
                                ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListFailure(unoPayResponse.getError().getMessage());
                            } else {
                                ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListFailure(Utils.getServerErrorMessage(response.code(),null,mContext,mContext.getString(R.string.default_network_error_message)));
                            }

                        }
                    } else {
                        //TODO handle the error to pass the data to the caller
                        ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListFailure(mContext.getString(R.string.default_network_error_message));
                    }
                }
            }

            @Override
            public void onFailure(Call<MasterWalletResponse> call, Throwable t) {
                if (mUnoPayServerCallBack != null) {
                    //TODO handle the error to pass the data to the caller
                    ((GetWalletListCallBack) mUnoPayServerCallBack).onGetWalletListFailure(Utils.getFailureError(t, mContext));
                }
            }
        });
    }

    public void requestOTPForTransaction(final OTPRequestParams otpRequestParams) {
        String checkSum = Utils.getSha1Value(otpRequestParams.mData.mSdkApiKey + otpRequestParams.mData.toString() + otpRequestParams.mData.mSdkApiKey);
        final Call<TransactionResponse> requestOTPCall = mUPPSAPIs.requestOTPForTransaction(checkSum, otpRequestParams.mData.mPartnerId, otpRequestParams.mData.mSdkApiKey, otpRequestParams);
        requestOTPCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (mUnoPayServerCallBack != null) {
                    if (response != null) {

                        if (response.isSuccessful()) {
                            TransactionResponse transactionResponse = response.body();
                            if (transactionResponse != null) {
                                //Set transaction ID here if we need to to ask for resend OTP for the same transaction
                                otpRequestParams.mData.mTransactionId = transactionResponse.data.mTransactionId;
                                ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestSuccess(otpRequestParams);
                            } else {
                                UnoPayResponse unoPayResponse = new UnoPayResponse();
                                UnoPayErrorResponse errorResponse = new UnoPayErrorResponse();
                                errorResponse.setMessage(mContext.getString(R.string.default_network_error_message));
                                unoPayResponse.setError(errorResponse);
                                ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestFailed(otpRequestParams, unoPayResponse.getError().getMessage());
                            }
                        } else {

                            Gson gson = new Gson();
                            UnoPayResponse unoPayResponse = null;

                            String error = null;
                            try {
                                error = response.errorBody().string();
                                if (error != null) {
                                    unoPayResponse =
                                            new Gson().fromJson(error, UnoPayResponse.class);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception ex) {

                            }


                            if (unoPayResponse != null) {
                                ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestFailed(otpRequestParams, unoPayResponse.getError().getMessage());
                            } else {
                                ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestFailed(otpRequestParams, Utils.getServerErrorMessage(response.code(),null,mContext,mContext.getString(R.string.default_network_error_message)));
                            }
                        }

                    } else {
                        ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestFailed(otpRequestParams, mContext.getString(R.string.default_network_error_message));
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                if (mUnoPayServerCallBack != null) {
                    ((RequestOTPCallback) mUnoPayServerCallBack).onOTPRequestFailed(otpRequestParams, Utils.getFailureError(t, mContext));
                }

            }
        });

    }


    public void verifyOtpForTransaction(VerifyOTPRequest verifyOTP) {
        String checkSum = Utils.getSha1Value(verifyOTP.data.sdkApiKey + verifyOTP.data.toString() + verifyOTP.data.sdkApiKey);
        Call<TransactionResponse> verifyOtpCall = mUPPSAPIs.verifyOTPForTransaction(checkSum, verifyOTP.data.partnerId, verifyOTP.data.sdkApiKey, verifyOTP);
        verifyOtpCall.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (mUnoPayServerCallBack != null) {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            TransactionResponse transactionResponse = response.body();
                            if (transactionResponse != null) {
                                ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPSuccess(transactionResponse);
                            } else {
                                UnoPayResponse unoPayResponse = new UnoPayResponse();
                                UnoPayErrorResponse errorResponse = new UnoPayErrorResponse();
                                errorResponse.setMessage(mContext.getString(R.string.default_network_error_message));
                                unoPayResponse.setError(errorResponse);
                                ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(unoPayResponse);
                            }
                        } else {

                            if (response.body() == null) {

                                Gson gson = new Gson();
                                UnoPayResponse unoPayResponse = null;

                                String error = null;
                                try {
                                    error = response.errorBody().string();
                                    if (error != null) {
                                        unoPayResponse =
                                                new Gson().fromJson(error, UnoPayResponse.class);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception ex) {

                                }


                                if (unoPayResponse != null) {
                                    ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(unoPayResponse);
                                } else {

                                    unoPayResponse = new UnoPayResponse();
                                    unoPayResponse.code = response.code();
                                    UnoPayErrorResponse errorResponse = new UnoPayErrorResponse();
                                    errorResponse.setMessage(Utils.getServerErrorMessage(response.code(),null,mContext,mContext.getString(R.string.default_network_error_message)));
                                    unoPayResponse.setError(errorResponse);
                                    ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(unoPayResponse);
                                }
                            } else {
                                ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(response.body());
                            }

                        }
                    } else {
                        UnoPayResponse unoPayResponse = new UnoPayResponse();
                        unoPayResponse.message = mContext.getString(R.string.default_network_error_message);
                        UnoPayErrorResponse errorResponse = new UnoPayErrorResponse();
                        errorResponse.setMessage(mContext.getString(R.string.default_network_error_message));
                        unoPayResponse.setError(errorResponse);
                        ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(unoPayResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                if (mUnoPayServerCallBack != null) {
                    UnoPayResponse unoPayResponse = new UnoPayResponse();
                    UnoPayErrorResponse unoPayErrorResponse = new UnoPayErrorResponse();
                    unoPayErrorResponse.setMessage(Utils.getFailureError(t, mContext));
                    unoPayResponse.setError(unoPayErrorResponse);
                    ((VerifyOTPCallBack) mUnoPayServerCallBack).onVerifyOTPFailure(unoPayResponse);
                }
            }
        });

    }
}

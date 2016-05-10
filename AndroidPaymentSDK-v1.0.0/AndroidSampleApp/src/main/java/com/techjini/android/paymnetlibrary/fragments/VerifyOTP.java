package com.techjini.android.paymnetlibrary.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.UnoPayParams;
import com.techjini.android.paymnetlibrary.Utility.SmsParser;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.interfaces.FragmentInteractionListener;
import com.techjini.android.paymnetlibrary.interfaces.RequestOTPCallback;
import com.techjini.android.paymnetlibrary.interfaces.VerifyOTPCallBack;
import com.techjini.android.paymnetlibrary.network.UnoPayServerTransactions;
import com.techjini.android.paymnetlibrary.network.model.PushNotification;
import com.techjini.android.paymnetlibrary.network.model.Validation;
import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;
import com.techjini.android.paymnetlibrary.network.model.request.VerifyOTPRequest;
import com.techjini.android.paymnetlibrary.network.model.response.TransactionResponse;
import com.techjini.android.paymnetlibrary.network.model.response.UnoPayResponse;
import com.techjini.android.paymnetlibrary.views.progressbar.SmoothProgressBar;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVerifyOTPInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerifyOTP#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyOTP extends BaseFragment implements View.OnClickListener, VerifyOTPCallBack, RequestOTPCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String OTP_REQUEST_PARAM = "otpRequestParam";
    private static final String PARAMS = "unopay_params";

    // TODO: Rename and change types of parameters
    private OTPRequestParams mOtpRequestParams;
    private UnoPayParams mUnoPayParams;


    private OnVerifyOTPInteractionListener mListener;

    private TextView mUnoPayBenefits, mVerifyOtpText, mResendOTP;

    private EditText otpEdit;
    private SmoothProgressBar mProgressBar;

    private CountDownTimer mCountDownTimer;

    private int OTP_LENGTH = 6;

    private TextView done;
    private UnoPayServerTransactions unoPayServerTransactions;

    public VerifyOTP() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param otpRequestParams Parameter 1.
     * @return A new instance of fragment VerifyOTP.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyOTP newInstance(OTPRequestParams otpRequestParams, UnoPayParams unoPayParams) {
        VerifyOTP fragment = new VerifyOTP();
        Bundle args = new Bundle();
        args.putSerializable(OTP_REQUEST_PARAM, otpRequestParams);
        args.putSerializable(PARAMS, unoPayParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOtpRequestParams = (OTPRequestParams) getArguments().getSerializable(OTP_REQUEST_PARAM);
            mUnoPayParams = (UnoPayParams) getArguments().getSerializable(PARAMS);
            unoPayServerTransactions = new UnoPayServerTransactions(getContext(), this, mUnoPayParams.isProduction());

        }

    }

    private void startTimer() {
        if (mCountDownTimer != null) {
            return;
        }
        mCountDownTimer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                stopTimer();
                showProgressBar(false);
                enableResend(true);
            }
        }

        ;
        mCountDownTimer.start();
    }

    private void enableResend(boolean enable) {

        mResendOTP.setEnabled(enable);
        mResendOTP.setTextColor(enable ? Color.parseColor("#4cb760") : Color.parseColor("#808080"));
    }


    private void stopTimer() {
        if (mCountDownTimer == null) {
            return;
        }
        mCountDownTimer.cancel();
        mCountDownTimer = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_verify_otp, container, false);

        /*mLoadingText = (TextView) rootView.findViewById(R.id.loading_text);*/
        mUnoPayBenefits = (TextView) rootView.findViewById(R.id.unopay_benefits);
        mUnoPayBenefits.setVisibility(View.GONE);
        mVerifyOtpText = (TextView) rootView.findViewById(R.id.waitingForOtpText);
        mProgressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress_bar);
        mResendOTP = (TextView) rootView.findViewById(R.id.resend_otp);

        TextView mobileNumber = (TextView) rootView.findViewById(R.id.otp_sent_mobile);
        done = (TextView) rootView.findViewById(R.id.done_button);
        otpEdit = (EditText) rootView.findViewById(R.id.otp_text);
        mobileNumber.setText(mOtpRequestParams.mData.mMobile);

        done.setOnClickListener(this);
        mResendOTP.setOnClickListener(this);
        enableResend(false);
        startTimer();
        return rootView;
    }

    private void verifyOTP() {
        Validation validation = Utils.isValidOtp(otpEdit.getText().toString());
        if (validation.mIsValid) {
            showProgressBar(false);
            if (mListener != null) {


                if (Utils.isOnline(getContext())) {
                    mListener.isServerTransaction(true);
                    showLoading("Verifying OTP");
                    VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest(getContext());
                    verifyOTPRequest.data.mobile = mOtpRequestParams.mData.mMobile;
                    verifyOTPRequest.data.otp = otpEdit.getText().toString();
                    verifyOTPRequest.data.partnerId = mOtpRequestParams.mData.mPartnerId;
                    verifyOTPRequest.data.sdkApiKey = mOtpRequestParams.mData.mSdkApiKey;
                    verifyOTPRequest.data.transactionId = mOtpRequestParams.mData.mTransactionId;
                    verifyOTPRequest.data.walletId = mOtpRequestParams.mData.mWalletId;
                    unoPayServerTransactions.verifyOtpForTransaction(verifyOTPRequest);
                } else {
                    mListener.isServerTransaction(false);

                    showDialog(getString(R.string.label_error), getString(R.string.no_internet), "OK", null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                }


            }
        } else {
            otpEdit.setError(validation.mMessage);
        }
    }


    BroadcastReceiver OTPReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            PushNotification notification = SmsParser.processSMS(intent, context);
            if (notification != null) {
                onMessageReceived(notification);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null && !getActivity().isFinishing()) {

            getActivity().registerReceiver(OTPReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null && !getActivity().isFinishing()) {
            try {
                getActivity().unregisterReceiver(OTPReceiver);
            } catch (IllegalArgumentException e) {
                //broadcast listener already unregistered
            }

        }
    }


    public void onMessageReceived(PushNotification notification) {
        if (!getActivity().isFinishing()) {
            String message = notification.parameters.message;
            if (message != null) {
                List<String> otpText = Arrays.asList(message.trim().split(" "));
                for (int i = 0; i < otpText.size(); i++) {
                    String otp = otpText.get(i);
                    if (otp.trim().length() == OTP_LENGTH) {
                        otpEdit.setText(otp.trim());
                        showProgressBar(false);
                        try {
                            getActivity().unregisterReceiver(OTPReceiver);
                        } catch (IllegalArgumentException e) {
                            //broadcast listener already unregistered
                        }

                        verifyOTP();
                        break;
                    }

                }
            }
        } else {


            if (getActivity() != null && !getActivity().isFinishing()) {
                try {
                    getActivity().unregisterReceiver(OTPReceiver);
                } catch (IllegalArgumentException e) {
                    //broadcast listener already unregistered
                }
            }
        }
    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mVerifyOtpText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private ProgressDialog progressDialog = null;

    private void showLoading(String loadingText) {
        setEditableFields(true);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(loadingText);
        progressDialog.show();

    }

    private void hideLoading() {
        setEditableFields(false);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void setEditableFields(boolean show) {

        done.setEnabled(!show);
        otpEdit.setEnabled(!show);
        enableResend(!show);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerifyOTPInteractionListener) {
            mListener = (OnVerifyOTPInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVerifyOTPInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.done_button) {
            verifyOTP();
        }
        if (v.getId() == R.id.resend_otp) {
            if (mListener != null) {
                if (Utils.isOnline(getContext())) {
                    showLoading("Requesting OTP...");
                    mListener.isServerTransaction(true);
                    unoPayServerTransactions.requestOTPForTransaction(mOtpRequestParams);
                } else {
                    mListener.isServerTransaction(false);
                    showDialog(getString(R.string.label_error), getString(R.string.no_internet), "OK", null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, null);
                }
            }
        }

    }

    @Override
    public void onVerifyOTPSuccess(UnoPayResponse transactionResponse) {
        hideLoading();
        if (mListener != null) {
            mListener.onTransactionComplete((TransactionResponse) transactionResponse);
            mListener.isServerTransaction(false);
        }
    }

    @Override
    public void onVerifyOTPFailure(UnoPayResponse transactionResponse) {
        hideLoading();
        if (mListener != null) {
            mListener.isServerTransaction(false);
        }
        String errorMessage = "";
        if (transactionResponse != null && transactionResponse.getError() != null && transactionResponse.getError().getMessage() != null) {
            errorMessage = transactionResponse.getError().getMessage();
        } else {
            errorMessage = getString(R.string.default_network_error_message);
        }
        showDialog(getString(R.string.label_error), errorMessage, "OK", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, null);
    }

    @Override
    public void onOTPRequestSuccess(OTPRequestParams otpRequestParams) {
        hideLoading();
        enableResend(false);
        showProgressBar(true);
        startTimer();
        if (mListener != null) {
            mListener.isServerTransaction(false);
        }

    }

    @Override
    public void onOTPRequestFailed(OTPRequestParams otpRequestParams, String errorResponse) {
        hideLoading();
        enableResend(true);
        showProgressBar(false);
        if (mListener != null) {
            mListener.isServerTransaction(false);
        }
    }

    public interface OnVerifyOTPInteractionListener extends FragmentInteractionListener {
        void onTransactionComplete(TransactionResponse transactionResponse);
    }
}


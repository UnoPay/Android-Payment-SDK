package com.techjini.android.paymnetlibrary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.UnoPayParams;
import com.techjini.android.paymnetlibrary.Utils;
import com.techjini.android.paymnetlibrary.Wallet;
import com.techjini.android.paymnetlibrary.interfaces.FragmentInteractionListener;
import com.techjini.android.paymnetlibrary.interfaces.RequestOTPCallback;
import com.techjini.android.paymnetlibrary.network.UnoPayServerTransactions;
import com.techjini.android.paymnetlibrary.network.model.Validation;
import com.techjini.android.paymnetlibrary.network.model.request.OTPRequestParams;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWalletSigInListener} interface
 * to handle interaction events.
 * Use the {@link WalletSignIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletSignIn extends BaseFragment implements RequestOTPCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WALLET = "wallet";
    private static final String MOBILE = "mobile";
    private static final String PARAMS = "params";


    // TODO: Rename and change types of parameters
    private Wallet mWallet;
    private UnoPayParams mUnoPayParams;

    private OnWalletSigInListener mListener;


    /*private RelativeLayout mLoadingLayout;*/

    /*private TextView mLoadingText;*/

    private UnoPayServerTransactions unoPayServerTransactions;

    // TODO: Rename and change types and number of parameters
    public static WalletSignIn newInstance(Wallet wallet, String mobileNumber, UnoPayParams unoPayParams) {
        WalletSignIn fragment = new WalletSignIn();
        Bundle args = new Bundle();
        args.putSerializable(WALLET, wallet);
        args.putString(MOBILE, mobileNumber);
        args.putSerializable(PARAMS, unoPayParams);
        fragment.setArguments(args);
        return fragment;
    }

    public WalletSignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWallet = (Wallet) getArguments().getSerializable(WALLET);
            mobileNumber = getArguments().getString(MOBILE);
            mUnoPayParams = (UnoPayParams) getArguments().getSerializable(PARAMS);
            unoPayServerTransactions = new UnoPayServerTransactions(getContext(), this, mUnoPayParams.isProduction());
        }

    }

    private Button payNow;
    private EditText mobileNumberET;
    private String mobileNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet_sign_in, container, false);
        payNow = (Button) rootView.findViewById(R.id.pay_now);
        /*mLoadingLayout = (RelativeLayout) rootView.findViewById(R.id.loading_layout);*/
        /*mLoadingText = (TextView) rootView.findViewById(R.id.loading_text);*/
        mobileNumberET = (EditText) rootView.findViewById(R.id.mobile_number);
        if (mobileNumber != null) {
            mobileNumberET.setText(mobileNumber);
        }
        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Validation validation = Utils.isValidMobileNumber(mobileNumberET.getText().toString());
                    if (validation.mIsValid) {

                        if (Utils.isOnline(getContext())) {
                            showLoading("Initiating transaction");
                            mListener.isServerTransaction(true);
                            OTPRequestParams otpRequestParams = new OTPRequestParams(getContext());
                            otpRequestParams.mData.mAmount = Utils.formatAmount(mUnoPayParams.getAmount());
                            otpRequestParams.mData.mAppTransactionId = Utils.getAppTransactionId(mUnoPayParams.getMerchantSdkKey());
                            otpRequestParams.mData.mMobile = mobileNumberET.getText().toString();
                            otpRequestParams.mData.mPartnerId = mUnoPayParams.getPartnerId();
                            otpRequestParams.mData.mSdkApiKey = mUnoPayParams.getMerchantSdkKey();
                            otpRequestParams.mData.mWalletId = String.valueOf(mWallet.getId());
                            otpRequestParams.mData.mTransactionId = "";
                            otpRequestParams.mData.mOrderId = "";
                            unoPayServerTransactions.requestOTPForTransaction(otpRequestParams);
                        } else {
                            mListener.isServerTransaction(false);
                            //TODO handle the dialog show to the user
                            showDialog(getString(R.string.label_error), getString(R.string.no_internet), "OK", null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null);
                        }
                    } else {
                        mobileNumberET.setError(validation.mMessage);
                    }
                }
            }
        });
        TextView walletDescription = (TextView) rootView.findViewById(R.id.wallet_detail_description);
        walletDescription.setText(String.format(getString(R.string.add_wallet_description), mWallet.getName()));
        return rootView;
    }

    /*private void setLoadingText(String loadingText) {
        *//*mLoadingText.setText(loadingText);*//*
    }*/

    private ProgressDialog progressDialog = null;

    private void showLoading(String loadingText) {
        //mLoadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(loadingText);
        progressDialog.setCancelable(false);
        progressDialog.show();

        payNow.setClickable(false);
        mobileNumberET.setEnabled(false);

    }

    private void hideLoading() {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        payNow.setClickable(true);
        mobileNumberET.setEnabled(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWalletSigInListener) {
            mListener = (OnWalletSigInListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWalletSigInListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onOTPRequestSuccess(OTPRequestParams otpRequestParams) {
        hideLoading();
        if (mListener != null) {
            mListener.onOTPSuccess(otpRequestParams);
            mListener.isServerTransaction(false);
        }
    }

    @Override
    public void onOTPRequestFailed(OTPRequestParams otpRequestParams, String errorResponse) {
        hideLoading();
        if (mListener != null) {
            mListener.isServerTransaction(false);
        }
        showDialog(getString(R.string.label_error), errorResponse, "OK", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, null);
    }


    public interface OnWalletSigInListener extends FragmentInteractionListener {
        // TODO: Update argument type and name
        void onOTPSuccess(OTPRequestParams otpRequestParams);

    }
}

package com.techjini.android.paymnetlibrary.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Nitin S.Mesta on 2/5/16.
 * // Copyright (c) 2016 Techjini Solutions. All rights reserved.
 */
public class BaseFragment extends Fragment {
    protected AlertDialog mDialog;

    public void showDialog(String title, String message, String okText, String cancelText, Dialog.OnClickListener positiveOnClickListener, Dialog.OnClickListener negativeOnClickListener) {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(getContext()).create();
        }

        mDialog.setTitle(title);
        mDialog.setMessage(message);

        if (okText != null && positiveOnClickListener != null) {
            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, okText, positiveOnClickListener);
        }

        if (cancelText != null && negativeOnClickListener != null) {
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelText, negativeOnClickListener);
        }

        if (getActivity() != null && !getActivity().isFinishing()) {
            mDialog.show();
        }

    }
}

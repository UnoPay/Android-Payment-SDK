package com.techjini.android.paymnetlibrary.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Bheema on 14/09/15.
 */
public class ListItemBaseView extends RelativeLayout {


    public ListItemBaseView(Context context) {
        super(context);
    }

    public ListItemBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListItemBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showToast(String message, int length) {
        Toast toast = Toast.makeText(getContext(), message, length);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
        toast.show();
    }


    AlertDialog alertDialog;

    public void showDialog(DialogInterface.OnClickListener okClickListener, DialogInterface.OnClickListener cancelListener, String okText, String cancelText, String title, String message, boolean isCancelOnOutsideTouch) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message);
        if (!isCancelOnOutsideTouch) {
            alertDialogBuilder.setCancelable(false);
        } else {
            alertDialogBuilder.setCancelable(true);
        }
        alertDialogBuilder.setPositiveButton(okText, okClickListener)
                .setNegativeButton(cancelText, cancelListener);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }

    public void showDialog(DialogInterface.OnClickListener okClickListener, String okText, String title, String message, boolean isCancelOnOutsideTouch) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message);
        if (!isCancelOnOutsideTouch) {
            alertDialogBuilder.setCancelable(false);
        } else {
            alertDialogBuilder.setCancelable(true);
        }
        if (okClickListener == null) {
            okClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            };
        }
        alertDialogBuilder.setPositiveButton(okText, okClickListener);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}

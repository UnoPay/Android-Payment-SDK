package com.techjini.android.paymnetlibrary.Utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.telephony.SmsMessage;

import com.techjini.android.paymnetlibrary.R;
import com.techjini.android.paymnetlibrary.constants.UPConstants;
import com.techjini.android.paymnetlibrary.network.model.PushNotification;


/**
 * Created by Bheema on 14/03/16.
 * Company Techjini
 */
public class SmsParser {
    public static PushNotification processSMS(Intent intent, Context context) {


        if (intent != null) {
            try {
                final Bundle bundle = intent.getExtras();
                Object pdus[] = (Object[]) bundle.get("pdus");
                if (pdus != null) {

                    for (int i = 0; i < pdus.length; i++) {
                        String format = intent.getStringExtra("format");
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        phoneNumber = phoneNumber.toUpperCase();
                        if (UPConstants.OTP_SMS_NAMES.toUpperCase().contains(phoneNumber.substring(phoneNumber.lastIndexOf("-") + 1, phoneNumber.length()))) {

                            String message = currentMessage.getDisplayMessageBody();
                            if (message.startsWith(context.getResources().getString(R.string.offline_otp_start_with))) {
                                PushNotification notification = new PushNotification();
                                notification.type = UPConstants.TYPE_OFFLINE_OTP;
                                PushNotification.Parameters parameters = notification.new Parameters();
                                parameters.message = message.trim();
                                notification.parameters = parameters;
                                return notification;
                            } else {
                                message = message.replaceAll("[^0-9]+", " ");
                                PushNotification notification = new PushNotification();
                                PushNotification.Parameters parameters = notification.new Parameters();
                                parameters.message = message.trim();
                                notification.parameters = parameters;
                                notification.parameters.otpFrom = getOtpFrom(phoneNumber);
                                return notification;
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    private static String getOtpFrom(String phoneNumber) {
        if (phoneNumber.contains("PAYUMN")) {
            return "PAYUMN";
        } else if (phoneNumber.contains("MYUDIO") || phoneNumber.contains("SHMART")) {
            return "MYUDIO";
        } else if (phoneNumber.contains("MOBIKW")) {
            return "MOBIKW";
        } else {
            return "UNOPAY";
        }
    }
}

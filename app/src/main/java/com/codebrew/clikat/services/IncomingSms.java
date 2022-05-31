package com.codebrew.clikat.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.codebrew.clikat.retrofit.Config;

public class IncomingSms extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (senderNum.equals(Config.Companion.getSenderNumber())) {
                            Intent intent1 = new Intent();
                            intent1.setAction("OTPMESSAGE");
                            intent1.putExtra("OtpMessage", message);
                            context.sendBroadcast(intent1);
                        }

                    } catch (Exception e) {
                    e.printStackTrace();
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

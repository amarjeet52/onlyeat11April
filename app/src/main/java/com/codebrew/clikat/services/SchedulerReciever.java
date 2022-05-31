package com.codebrew.clikat.services;

/*
 * Created by cbl80 on 9/9/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.codebrew.clikat.R;
import com.codebrew.clikat.module.bottom_navigation.MainScreenActivity;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.configurations.Configurations;

public class SchedulerReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent2) {
        if (StaticFunction.INSTANCE.cartCount(context)!=0) {
            int requestID = (int) System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(context, MainScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("isNotification","12344321");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID,
                    intent, 0);
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.order_pending, Configurations.strings.order)).setContentText(context.getString(R.string.basket_message))
                    .setSmallIcon(getNotificationIcon()).setTicker(context.getString(R.string.basket_message))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.basket_message)))
                    .setWhen(System.currentTimeMillis())
                    .setColor(ContextCompat.getColor(context, R.color.theme_color))
                    .setContentIntent(pendingIntent).setAutoCancel(true)
                    .build();
            notificationManager.notify(requestID, notification);
        }
    }

    private int getNotificationIcon() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_launcher_new : R.mipmap.ic_launcher_new;
    }
}

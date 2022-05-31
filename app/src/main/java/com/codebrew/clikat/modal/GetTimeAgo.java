package com.codebrew.clikat.modal;

import android.content.Context;

import com.codebrew.clikat.R;

/*
 * Created by cbl80 on 9/6/16.
 */
public class GetTimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time,Context context) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now =System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.a_min_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + context.getString(R.string.min_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.a_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + context.getString(R.string.hour_ago);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getString(R.string.yest);
        } else {
            return diff / DAY_MILLIS + context.getString(R.string.days_afo);
        }
    }

}
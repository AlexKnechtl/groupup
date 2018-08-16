package com.example.alexander.groupup;

import android.app.Application;
import android.content.Context;

public class GetTimeStrings extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeStrings(long time, Context ctx) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        if (time > now || time <= 0 || time == now) {
            return ctx.getString(R.string.today);
        }

        final long diff = now - time;
        if (diff < 24 * HOUR_MILLIS) {
            return ctx.getString(R.string.today);
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getString(R.string.yesterday);
        } else if (diff < 7 * DAY_MILLIS) {
            return ctx.getString(R.string.this_week);
        } else if (diff < 24 * DAY_MILLIS) {
            return ctx.getString(R.string.this_month);
        } else {
            return ctx.getString(R.string.earlier);
        }
    }
}
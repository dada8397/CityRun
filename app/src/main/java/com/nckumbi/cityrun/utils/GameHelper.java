package com.nckumbi.cityrun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nckumbi.cityrun.R;

/**
 * Created by user on 2016/5/22.
 */
public class GameHelper {
    public static final int EXPIRED_RESULT_CODE = 65535;
    public static final Long AVAILABLE_DURATION = 3600 * 3L;

    public static void saveStartTime(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        if (!preferences.contains(serial)) {
            preferences.edit().putLong(serial, System.currentTimeMillis() / 1000L).apply();
        }
    }

    public static Long getStartTime(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        return preferences.getLong(serial, System.currentTimeMillis() / 1000L);
    }

    public static Long getElapsedTime(Long startTime) {
        Long currentTime = System.currentTimeMillis() / 1000L;

        return currentTime - startTime;
    }
}

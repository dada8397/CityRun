package com.nckumbi.cityrun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nckumbi.cityrun.R;

/**
 * Created by user on 2016/5/22.
 */
public class GameHelper {
    public static final int QRCODE_SCANNER_REQUEST_CODE = 0x0001;
    public static final int QRCODE_EXPIRED_RESULT_CODE = 0x0002;
    public static final int QRCODE_VALID_RESULT_CODE = 0x0003;

    public static final Long AVAILABLE_DURATION = 3600 * 3L;

    public static final String PREF_CURRENT_SERIAL = "serial";

    public static void saveCurrentSerial(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        preferences.edit().putString(PREF_CURRENT_SERIAL, serial).apply();
    }

    public static String getCurrentSerial(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        return preferences.getString(PREF_CURRENT_SERIAL, null);
    }

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

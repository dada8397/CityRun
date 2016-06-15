package com.nckumbi.cityrun.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.TextView;

import com.nckumbi.cityrun.R;

import java.util.TimerTask;

/**
 * Created by user on 2016/5/22.
 */
public class GameHelper {
    public static final int ACTIVITY_REQUEST_CODE = 0x000;
    public static final int QRCODE_SCANNER_REQUEST_CODE = 0x0001;
    public static final int QRCODE_EXPIRED_RESULT_CODE = 0x0002;
    public static final int QRCODE_VALID_RESULT_CODE = 0x0003;

    public static final long AVAILABLE_DURATION = 3600 * 3L;

    public static final String PREF_CURRENT_SERIAL = "serial";

    public static void saveCurrentSerial(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        if (serial == null || serial.isEmpty()) {
            preferences.edit().remove(PREF_CURRENT_SERIAL).apply();
        } else {
            preferences.edit().putString(PREF_CURRENT_SERIAL,
                    Base64.encodeToString(serial.getBytes(), Base64.NO_WRAP)).apply();
        }
    }

    public static String getCurrentSerial(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        String result = preferences.getString(PREF_CURRENT_SERIAL, null);
        if (result != null) {
            result = new String(Base64.decode(result, Base64.NO_WRAP));
        }

        return result;
    }

    public static void saveStartTime(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        String base64Serial = Base64.encodeToString(serial.getBytes(), Base64.NO_WRAP);
        if (!preferences.contains(base64Serial)) {
            preferences.edit().putLong(base64Serial, getNowTimestamp()).apply();
        }
    }

    public static long getStartTime(Context context, String serial) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        return preferences.getLong(
                Base64.encodeToString(serial.getBytes(), Base64.NO_WRAP), getNowTimestamp());
    }

    public static long getElapsedTime(Long startTime) {
        return getNowTimestamp() - startTime;
    }

    public static long getNowTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static class ExpiredCheckTask extends TimerTask {
        private AppCompatActivity activity;
        private TextView clockTextView;
        private long elapsedTime;

        public ExpiredCheckTask(AppCompatActivity activity, TextView clockTextView, long startTime) {
            this.activity = activity;
            this.clockTextView = clockTextView;
            this.elapsedTime = GameHelper.getElapsedTime(startTime);
        }

        @Override
        public void run() {
            ++elapsedTime;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clockTextView.setText(
                            String.format("%02d:%02d:%02d",
                                    elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60));

                    if (elapsedTime >= GameHelper.AVAILABLE_DURATION) {
                        activity.setResult(GameHelper.QRCODE_EXPIRED_RESULT_CODE);
                        activity.finish();
                    }
                }
            });
        }
    }
}

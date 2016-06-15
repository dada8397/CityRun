package com.nckumbi.cityrun;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nckumbi.cityrun.utils.GameHelper;
import com.nckumbi.cityrun.utils.Utils;

import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by DADA on 2016/6/10.
 * vincent8397@gmail.com
 */
public class QrCodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private static final int MY_CAMERA_PERMISSION_REQUEST_CODE = 1;

    private ZBarScannerView scannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            final View decorView = getWindow().getDecorView();
            final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_qr_code);

        scannerView = new ZBarScannerView(QrCodeActivity.this);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.qrCodeScannerView);
        if (contentFrame != null) {
            contentFrame.addView(scannerView);
        }

        // Request camera permission first
        int permissionCheck = ActivityCompat.checkSelfPermission(QrCodeActivity.this,
                Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QrCodeActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        scannerView.setResultHandler(QrCodeActivity.this);
        scannerView.startCamera();

        if (MainActivity.stopped) {
            MainActivity.player = new BackgroundMusicPlayer(QrCodeActivity.this, R.raw.main_bgm, true);
            MainActivity.player.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            MainActivity.stopped = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        scannerView.stopCamera();

        Context context = getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                MainActivity.player.cancel(true);
                MainActivity.stopped = true;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.player.cancel(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                QrCodeActivity.this.finish();
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        final String data = result.getContents();
        final Long startTime = GameHelper.getStartTime(QrCodeActivity.this, data);

        if (GameHelper.getElapsedTime(startTime) < GameHelper.AVAILABLE_DURATION) {
            // FIXME: The following is demo code.
            if (data.length() < 9 || !data.substring(0, 9).equals("startgame")) {
                scannerView.resumeCameraPreview(this);
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("serial", data);

            setResult(GameHelper.QRCODE_VALID_RESULT_CODE, intent);
            QrCodeActivity.this.finish();
        } else {
            QrCodeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showDialog(QrCodeActivity.this, "無效的 QRCode",
                            "此 QRCode 自掃描後已超過三小時，無法再度使用！");
                    scannerView.resumeCameraPreview(QrCodeActivity.this);
                }
            });
        }
    }
}

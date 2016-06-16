package com.nckumbi.cityrun;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nckumbi.cityrun.utils.ProfileHelper;
import com.nckumbi.cityrun.utils.Utils;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    GifMovieView mainBikeGif;
    GifMovieView mainMountainGif;
    ImageButton imageButton;
    protected static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_main);

        // Set gif resources
        mainBikeGif = (GifMovieView) findViewById(R.id.mainBikeGif);
        if (mainBikeGif != null) {
            mainBikeGif.setMovieResource(R.drawable.main_bike);
        }
        mainMountainGif = (GifMovieView) findViewById(R.id.mainMountainGif);
        if (mainMountainGif != null) {
            mainMountainGif.setMovieResource(R.drawable.main_mountain);
        }
        imageButton = (ImageButton) findViewById(R.id.regRegisterImageButton);
        if (imageButton != null) {
            imageButton.setOnClickListener(startLoginActivity);
        }

        context = getApplicationContext();
        BackgroundMusicService.start(MainActivity.this, R.raw.main_bgm, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainBikeGif.setPaused(true);
        mainMountainGif.setPaused(true);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                BackgroundMusicService.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBikeGif.setPaused(false);
        mainMountainGif.setPaused(false);

        if (BackgroundMusicService.isStopped()) {
            BackgroundMusicService.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainBikeGif.setPaused(true);
        mainMountainGif.setPaused(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BackgroundMusicService.clean();
    }

    protected View.OnClickListener startLoginActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Main activity", "Touch screen");

            String uuid = ProfileHelper.getCurrentUuid(MainActivity.this);
            if (uuid != null) {
                Log.d("Main activity", "Found saved uuid");

                loginWithUuid(uuid);
            } else {
                Log.d("Main activity", "Request to login");

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    };

    private void loginWithUuid(String uuid) {
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
                "請稍後", "等待伺服器回應中...", true);

        ProfileHelper.query(uuid, new ProfileHelper.Callback() {
            @Override
            public void onComplete(JSONObject result) {
                progressDialog.dismiss();

                if (result == null) {
                    Toast.makeText(MainActivity.this, R.string.msg_network_error, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Intent intent = new Intent();

                        if (result.getBoolean("result")) {
                            intent.setClass(MainActivity.this, GameMenuActivity.class);
                            intent.putExtras(Utils.JsonToBundle(result));
                        } else {
                            intent.setClass(MainActivity.this, LoginActivity.class);
                        }

                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}

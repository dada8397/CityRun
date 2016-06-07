package com.nckumbi.cityrun;

import android.app.ActivityManager;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected static BackgroundMusicPlayer player;
    GifMovieView mainBikeGif;
    GifMovieView mainMountainGif;
    ImageButton imageButton;
    protected static Boolean stopped;
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
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        if (imageButton != null) {
            imageButton.setOnClickListener(startLoginActivity);
        }

        player = new BackgroundMusicPlayer(MainActivity.this, R.raw.main_bgm, true);
        player.execute();
        stopped = false;

        context = getApplicationContext();
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
                player.cancel(true);
                stopped = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBikeGif.setPaused(false);
        mainMountainGif.setPaused(false);
        if(stopped) {
            player = new BackgroundMusicPlayer(MainActivity.this, R.raw.main_bgm, true);
            player.execute();
            stopped = false;
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
        player.cancel(true);
    }

    protected View.OnClickListener startLoginActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Main activity", "Touch screen");
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };
}

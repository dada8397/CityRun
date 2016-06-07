package com.nckumbi.cityrun;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    GifMovieView mainBikeGif;
    GifMovieView mainMountainGif;
    BackgroundMusicPlayer player;
    ImageButton imageButton;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainBikeGif.setPaused(true);
        mainMountainGif.setPaused(true);
        player.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBikeGif.setPaused(false);
        mainMountainGif.setPaused(false);
        player = new BackgroundMusicPlayer(MainActivity.this, R.raw.main_bgm, true);
        player.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected View.OnClickListener startLoginActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Main activity", "Touch screen");

        }
    };
}

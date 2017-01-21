package com.nckumbi.cityrun;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nckumbi.cityrun.utils.GameHelper;

import java.util.List;
import java.util.Timer;

/**
 * Created by DADA on 2016/6/14.
 * vincent8397@gmail.com
 */
public class ChapterSelectActivity extends AppCompatActivity {

    GifMovieView chapterSelectGif;
    TextView chapterSelectClock;
    TextView chapterSelectProgress;
    TextView chapterSelectGold;
    ImageButton chapterSelectGameStartImageButton;
    ImageButton chapterSelectBackImageButton;
    ImageButton chapterSelectMenuImageButton;
    ImageView chapterZeroImageView;
    ImageView chapterOneImageView;
    ImageView chapterTwoImageView;
    ImageView chapterThreeImageView;
    ImageView chapterFourImageView;

    private String currentSerial;
    private Timer expiredCheckTimer;

    protected int nowChapter;
    protected String nowPlace;

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
        setContentView(R.layout.activity_chapter_select);

        chapterSelectGif = (GifMovieView) findViewById(R.id.chapterSelectGif);
        if (chapterSelectGif != null) {
            chapterSelectGif.setMovieResource(R.drawable.game_menu_level_seekret_market);
        }

        chapterSelectClock = (TextView) findViewById(R.id.chapterSelectClock);
        chapterSelectProgress = (TextView) findViewById(R.id.chapterSelectProgress);
        chapterSelectGold = (TextView) findViewById(R.id.chapterSelectGold);

        chapterSelectGameStartImageButton = (ImageButton) findViewById(R.id.chapterSelectGameStartImageButton);
        chapterSelectBackImageButton = (ImageButton) findViewById(R.id.chapterSelectBackImageButton);
        chapterSelectMenuImageButton = (ImageButton) findViewById(R.id.chapterSelectMenuImageButton);

        chapterSelectGameStartImageButton.setOnClickListener(gameStartClicked);

        currentSerial = GameHelper.getCurrentSerial(ChapterSelectActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getApplicationContext().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        nowPlace = "seekret_market";
        nowChapter = sharedPreferences.getInt("seekret_market", 0);
        if (nowChapter == 0) {
            sharedPreferences.edit().putInt("seekret_market", nowChapter).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicService.start(R.raw.main_bgm, true);

//        long serialStartTime = GameHelper.getStartTime(ChapterSelectActivity.this, currentSerial);
//
//        if (serialStartTime != GameHelper.getNowTimestamp()) {
//            expiredCheckTimer = new Timer(true);
//            expiredCheckTimer.schedule(new GameHelper.ExpiredCheckTask(
//                    ChapterSelectActivity.this,
//                    chapterSelectClock,
//                    serialStartTime
//            ), 0, 1000);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context = getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                BackgroundMusicService.pause();
            }
        }

        if (expiredCheckTimer != null) {
            expiredCheckTimer.cancel();
            expiredCheckTimer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GameHelper.ACTIVITY_REQUEST_CODE) {
            if (resultCode == GameHelper.QRCODE_EXPIRED_RESULT_CODE) {
                setResult(resultCode);
                ChapterSelectActivity.this.finish();
            }
        }
    }

    protected View.OnClickListener gameStartClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            GameHelper.saveStartTime(ChapterSelectActivity.this, currentSerial);
//            if (expiredCheckTimer == null) {
//                expiredCheckTimer = new Timer(true);
//                expiredCheckTimer.schedule(new GameHelper.ExpiredCheckTask(
//                        ChapterSelectActivity.this,
//                        chapterSelectClock,
//                        GameHelper.getStartTime(ChapterSelectActivity.this, currentSerial)
//                ), 0, 1000);
//            }

            Intent intent = new Intent();
            intent.setClass(ChapterSelectActivity.this, GameQaActivity.class);
            startActivityForResult(intent, GameHelper.ACTIVITY_REQUEST_CODE);
            // Stop main activity bgm
            BackgroundMusicService.pause();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };
}

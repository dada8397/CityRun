package com.nckumbi.cityrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nckumbi.cityrun.utils.GameHelper;
import com.nckumbi.cityrun.utils.Utils;

import java.util.List;

/**
 * Created by DADA on 2016/6/8.
 * vincent8397@gmail.com
 */
public class GameMenuActivity extends AppCompatActivity {

    GifMovieView gameMenuLevelSeekretMarketGif;
    GifMovieView gameMenuCatGif;
    GifMovieView unlockedGif;
    ImageView gameMenuMask;
    ImageView gameMenuChainRight;
    ImageView gameMenuChainLeft;
    ImageView gameMenuUnlockedSloganImageView;
    ImageButton gameMenuMenuImageButton;
    ImageButton gameMenuEnterGameImageButton;
    ImageButton gameMenuQrCodeImageButton;
    ImageButton gameMenuLockImageButton;

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
        setContentView(R.layout.activity_game_menu);

        gameMenuLevelSeekretMarketGif = (GifMovieView) findViewById(R.id.gameMenuLevelSeekretMarketGif);
        if (gameMenuLevelSeekretMarketGif != null) {
            gameMenuLevelSeekretMarketGif.setMovieResource(R.drawable.game_menu_level_seekret_market);
        }

        gameMenuCatGif = (GifMovieView) findViewById(R.id.gameMenuCatGif);
        if (gameMenuCatGif != null) {
            gameMenuCatGif.setMovieResource(R.drawable.game_menu_cat);
        }

        unlockedGif = (GifMovieView) findViewById(R.id.unlockedGif);
        if (unlockedGif != null) {
            unlockedGif.setMovieResource(R.drawable.game_menu_unlocked);
            unlockedGif.setAlpha(0.0f);
        }

        gameMenuMask = (ImageView) findViewById(R.id.gameMenuMask);
        gameMenuChainRight = (ImageView) findViewById(R.id.gameMenuChainRightImageView);
        gameMenuChainLeft = (ImageView) findViewById(R.id.gameMenuChainLeftImageView);
        gameMenuUnlockedSloganImageView = (ImageView) findViewById(R.id.gameMenuUnlockedSloganImageView);
        if (gameMenuUnlockedSloganImageView != null) {
            gameMenuUnlockedSloganImageView.setAlpha(0.0f);
        }
        gameMenuMenuImageButton = (ImageButton) findViewById(R.id.gameMenuMenuImageButton);
        gameMenuEnterGameImageButton = (ImageButton) findViewById(R.id.gameMenuEnterGameImageButton);
        gameMenuQrCodeImageButton = (ImageButton) findViewById(R.id.gameMenuQrCodeImageButton);
        gameMenuLockImageButton = (ImageButton) findViewById(R.id.gameMenuLockImageButton);

        final Animation lockAnimation = AnimationUtils.loadAnimation(GameMenuActivity.this, R.anim.shaking);
        gameMenuLockImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameMenuLockImageButton.startAnimation(lockAnimation);
                MediaPlayer lockSound = MediaPlayer.create(getApplicationContext(), R.raw.lock);
                lockSound.start();
            }
        });

        initializeComponents();

        gameMenuQrCodeImageButton.setOnClickListener(qrCodeImageButtonClicked);
        gameMenuEnterGameImageButton.setOnClickListener(enterGameImageButtonOnClicked);
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
                MainActivity.player.cancel(true);
                MainActivity.stopped = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.stopped) {
            MainActivity.player = new BackgroundMusicPlayer(GameMenuActivity.this, R.raw.main_bgm, true);
            MainActivity.player.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            MainActivity.stopped = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GameHelper.QRCODE_SCANNER_REQUEST_CODE) {
            if (resultCode == GameHelper.QRCODE_VALID_RESULT_CODE) {
                unlockLevel(data.getStringExtra("serial"));
            }
        }
    }

    protected View.OnClickListener qrCodeImageButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(GameMenuActivity.this, QrCodeActivity.class);
            startActivityForResult(intent, GameHelper.QRCODE_SCANNER_REQUEST_CODE);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    protected  View.OnClickListener enterGameImageButtonOnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(GameMenuActivity.this, ChapterSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    protected void initializeComponents() {
        if (!isUnlocked()) {
            gameMenuMask.setVisibility(View.VISIBLE);
            gameMenuChainRight.setVisibility(View.VISIBLE);
            gameMenuChainLeft.setVisibility(View.VISIBLE);
            gameMenuQrCodeImageButton.setVisibility(View.VISIBLE);
            gameMenuLockImageButton.setVisibility(View.VISIBLE);
            gameMenuMask.setAlpha(1.0f);
            gameMenuChainRight.setAlpha(1.0f);
            gameMenuChainLeft.setAlpha(1.0f);
            gameMenuQrCodeImageButton.setAlpha(1.0f);
            gameMenuLockImageButton.setAlpha(1.0f);
        } else {
            gameMenuMask.setVisibility(View.GONE);
            gameMenuChainRight.setVisibility(View.GONE);
            gameMenuChainLeft.setVisibility(View.GONE);
            gameMenuQrCodeImageButton.setVisibility(View.GONE);
            unlockedGif.setVisibility(View.GONE);
            gameMenuLockImageButton.setVisibility(View.GONE);
            gameMenuUnlockedSloganImageView.setVisibility(View.GONE);
        }
    }

    protected Boolean isUnlocked() {
        String currentSerial = GameHelper.getCurrentSerial(GameMenuActivity.this);

        if (currentSerial == null) {
            return false;
        } else if (GameHelper.getElapsedTime(
                GameHelper.getStartTime(GameMenuActivity.this, currentSerial)) >= GameHelper.AVAILABLE_DURATION) {
            Utils.showDialog(GameMenuActivity.this, "無效的 QRCode",
                    "此 QRCode 自掃描後已超過三小時，無法再度使用！");
            return false;
        }

        return true;
    }

    // 掃描完 QrCode 執行
    protected void unlockLevel(String serial) {
        // Save current used barcode
        GameHelper.saveCurrentSerial(GameMenuActivity.this, serial);

        unlockedGif.animate()
                .alpha(1.0f)
                .setDuration(500)
                .start();

        gameMenuUnlockedSloganImageView.setAlpha(1.0f);
        Animation unlockedSloganAnimation = AnimationUtils.loadAnimation(GameMenuActivity.this, R.anim.zoom_in);
        gameMenuUnlockedSloganImageView.setAnimation(unlockedSloganAnimation);
        unlockedSloganAnimation.start();

        MediaPlayer unlockedSound = MediaPlayer.create(getApplicationContext(), R.raw.drum_roll);
        unlockedSound.start();

        gameMenuChainRight.animate()
                .translationX(3000.0f)
                .translationY(-1000.0f)
                .setDuration(3000)
                .start();
        gameMenuChainLeft.animate()
                .translationX(-3000.0f)
                .translationY(-1000.0f)
                .setDuration(3000)
                .start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameMenuMask.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuMask.setVisibility(View.GONE);
                            }
                        });
                gameMenuChainRight.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuChainRight.setVisibility(View.GONE);
                            }
                        });
                gameMenuChainLeft.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuChainLeft.setVisibility(View.GONE);
                            }
                        });
                gameMenuQrCodeImageButton.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuQrCodeImageButton.setVisibility(View.GONE);
                            }
                        });
                unlockedGif.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                unlockedGif.setVisibility(View.GONE);
                                unlockedGif.setPaused(true);
                            }
                        });
                gameMenuLockImageButton.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuLockImageButton.setVisibility(View.GONE);
                            }
                        });
                gameMenuUnlockedSloganImageView.animate()
                        .alpha(0.0f)
                        .setDuration(3000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameMenuUnlockedSloganImageView.setVisibility(View.GONE);
                            }
                        });
            }
        }, 2000);
    }
}

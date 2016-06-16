package com.nckumbi.cityrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DADA on 2016/6/14.
 * vincent8397@gmail.com
 */
public class GameDialogActivity extends AppCompatActivity {

    protected String[] chapterOneMsg;
    TextView gameDialogChapterName;
    TextView gameDialogCharacterName;
    TextView gameDialogMessage;
    Button gameDialogButton;
    ImageView gameDialogQuestionImageView;

    protected int count;

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
        setContentView(R.layout.activity_game_dialog);

        gameDialogChapterName = (TextView) findViewById(R.id.gameDialogChapterName);
        gameDialogCharacterName = (TextView) findViewById(R.id.gameDialogCharacterName);
        gameDialogMessage = (TextView) findViewById(R.id.gameDialogMessage);
        gameDialogButton = (Button) findViewById(R.id.gameDialogButton);
        gameDialogQuestionImageView = (ImageView) findViewById(R.id.gameDialogQuestionImageView);
        gameDialogQuestionImageView.setVisibility(View.GONE);
        gameDialogQuestionImageView.setAlpha(0.0f);

        assert gameDialogButton != null;
        gameDialogButton.setOnClickListener(dialogClicked);
        count = 1;

        chapterOneMsg = new String[]{
                "好熱",
                "好想在事務所裡吹冷氣啊……",
                "還以為沒落市場周邊應該很好找車位的，原來旁邊是國華街喔，唉",
                "終於找到入口了，這入口也太偏僻了吧！一般人會知道這種地方嗎？",
                "屋頂是鋼架和鐵皮？這裡真的是古蹟嗎？還是說我走錯了？",
                "啊",
                "這個磁磚，看起來像是舊式建築常用的……小口磚或是二丁掛磚嗎？",
                "要不是髒成這樣的話，應該就能確認了吧，唉"
        };

        setDialog("DADA", chapterOneMsg[0]);

        gameDialogChapterName.setText("序章");

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BackgroundMusicService.isStopped()) {
            BackgroundMusicService.start(R.raw.game_bgm_seekret_market, true);
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

    protected void setDialog(String character, String message) {
        gameDialogCharacterName.setText(character);
        gameDialogMessage.setText(message);
    }

    protected View.OnClickListener dialogClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (count < chapterOneMsg.length) {
                setDialog("DADA", chapterOneMsg[count]);
                count++;
            } else if (count == chapterOneMsg.length) {
                gameDialogQuestionImageView.setVisibility(View.VISIBLE);
                gameDialogQuestionImageView.animate()
                        .alpha(1.0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                gameDialogQuestionImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        GameDialogActivity.this.finish();
                                    }
                                });
                            }
                        });
                count++;
            } else {
            }
        }
    };
}

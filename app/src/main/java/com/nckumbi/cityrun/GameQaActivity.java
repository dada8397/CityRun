package com.nckumbi.cityrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
<<<<<<< Updated upstream
=======
import android.content.SharedPreferences;
import android.os.AsyncTask;
>>>>>>> Stashed changes
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
public class GameQaActivity extends AppCompatActivity {

    protected int questionCount;

    TextView gameQaQuestionNumber;
    TextView gameQaQuestion;
    TextView gameQaClock;
    EditText gameQaAnswer;
    ImageButton gameQaEnterImageButton;
    ImageView correctImage;
    ImageView incorrectImage;

    private String currentSerial;
    private Timer expiredCheckTimer;

    SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_game_qa);

        BackgroundMusicService.start(R.raw.game_bgm_seekret_market, true);

        gameQaQuestionNumber = (TextView) findViewById(R.id.gameQaQuestionNumber);
        gameQaQuestion = (TextView) findViewById(R.id.gameQaQuestion);
        gameQaClock = (TextView) findViewById(R.id.gameQaClock);
        gameQaAnswer = (EditText) findViewById(R.id.gameQaAnswer);
        gameQaEnterImageButton = (ImageButton) findViewById(R.id.gameQaEnterImageButton);
        correctImage = (ImageView) findViewById(R.id.correctImage);
        incorrectImage = (ImageView) findViewById(R.id.incorrectImage);
        correctImage.setAlpha(0.0f);
        incorrectImage.setAlpha(0.0f);

        gameQaEnterImageButton.setOnClickListener(submitAnswer);

        sharedPreferences = getSharedPreferences(
                getApplicationContext().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        questionCount = sharedPreferences.getInt("question_count", 1);
        if (questionCount == 1) {
            sharedPreferences.edit().putInt("question_count", questionCount).apply();
        }
        gameQaQuestionNumber.setText("Q" + questionCount);

        gameQaQuestion.setText(question(questionCount));

        currentSerial = GameHelper.getCurrentSerial(GameQaActivity.this);

        Intent intent = new Intent();
        intent.setClass(GameQaActivity.this, GameDialogActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();

        BackgroundMusicService.pause();

        if (expiredCheckTimer != null) {
            expiredCheckTimer.cancel();
            expiredCheckTimer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicService.start(R.raw.game_bgm_seekret_market, true);

        if (expiredCheckTimer == null) {
            expiredCheckTimer = new Timer(true);
            expiredCheckTimer.schedule(new GameHelper.ExpiredCheckTask(
                    GameQaActivity.this,
                    gameQaClock,
                    GameHelper.getStartTime(GameQaActivity.this, currentSerial)
            ), 0, 1000);
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

    private String question(int count) {
        String msg = "";
        switch (count) {
            case 1:
                msg = "西市場當初在建築時，有做非常明確的建築規劃，" +
                        "然而今天我們在逛西市場時，卻常常被這裡的各種岔路、" +
                        "小巷、出入口迷惑，而失去了方向。對外人來說，" +
                        "這裡的住址是非常難找的，請問門牌號碼14號的店家是？";
                break;
            case 2:
                msg = "除了幾個主要、明顯的出入口外，西市場也有許多小的出入口，" +
                        "若一不小心迷路了，或許可以從這些出入口繞出去，確認位置，" +
                        "其中有一條出入口就位於著名的「佳佳西市場旅店」旁邊，" +
                        "請問往佳佳旅店的小巷，一出去會看到台灣導演___的介紹";
                break;
            case 3:
                msg = "過去的西市場中央，曾經有個噴泉廣場，" +
                        "如今廣場本身變成住家後門，而兩個入口也被店面擋住，" +
                        "只留下出入口的小巷。請問往中央噴泉的小巷是門牌幾號之間？" +
                        "（兩個數字，小到大，用空白分隔）";
                break;
            case 4:
                msg = "二戰前的西市場，原本是販賣日常用品、雜貨的地方，" +
                        "但現在我們已經很難看到這些店家了，" +
                        "取而代之的是在戰後進入西市場的各種布行、西裝店，" +
                        "例如正隆布行，請問正隆布行是西元幾年創立？";
                break;
            case 5:
                msg = "雖然說今日的西市場以布匹、訂製西裝聞名，" +
                        "但西市場本身並不是只有布行和西裝行，" +
                        "請問我們目前所在的這一帶，唯一不是賣布的電家是？";
                break;
            case 6:
                msg = "儘管今天我們已經很難看到西市場的完整的原貌了，但從一些細節裡，" +
                        "還是能一窺究竟。西市場內部的建築，有一部分還保留著戰前的狀況，" +
                        "這點我們從二樓的窗戶可以發現，請問，大多數的店家，二樓有幾「格」窗戶？";
                break;
            case 7:
                msg = "而唯一一個二樓只有一扇窗的店家是？";
                break;
            case 8:
                msg = "隨著時光流逝，人心不古，一般店家二樓的窗戶大多加裝了鐵窗做為防竊之用，" +
                        "請問唯一一個二樓沒有用鐵窗的店家是？";
                break;
            case 9:
                msg = "除了窗戶之外，我們也可以從其他方面來看西市場的建築，例如天花板上圓圓的大洞，" +
                        "那是作為散熱通風之用的天窗，在那個沒有冷氣的年代，良好的通風是非常重要的，" +
                        "請問，從新玫瑰到弘益布坊前有幾個天窗？";
                break;
            case 10:
                msg = "另一個可以讓我們一窺西市場建築的，則是磁磚，這裡使用的二丁掛磚，" +
                        "是20世紀初日本建築常用的一種磁磚，台南放送所也是使用二丁掛磚，" +
                        "這種磁磚的特色在於可以模仿不同材質的紋理，像是木質紋理、紅磚紋理等等，" +
                        "現在也還有一些建築會使用二丁掛磚，請問，除了西市場、台南放送所外，" +
                        "台南還有哪些古蹟同樣使用二丁掛磚？";
                break;
        }
        return msg;
    }

    private String answer(int count) {
        String msg = "";
        switch (count) {
            case 1:
                msg = "金隆布坊";
                break;
            case 2:
                msg = "蔡明亮";
                break;
            case 3:
                msg = "9 11";
                break;
            case 4:
                msg = "1951";
                break;
            case 5:
                msg = "義龍磁器行";
                break;
            case 6:
                msg = "24";
                break;
            case 7:
                msg = "正隆布料專賣店";
                break;
            case 8:
                msg = "江順成";
                break;
            case 9:
                msg = "7";
                break;
            case 10:
                msg = "糖業試驗所";
                break;
        }
        return msg;
    }

    private View.OnClickListener submitAnswer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (questionCount > 10) {
                GameQaActivity.this.finish();
            } else {
                if (gameQaAnswer.getText().toString().equals(answer(questionCount))) {
                    questionCount++;
                    sharedPreferences.edit().putInt("question_count", questionCount).apply();
                    correctImage.setVisibility(View.VISIBLE);
                    correctImage.animate()
                            .alpha(1.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            });
                    correctImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            correctImage.setAlpha(0.0f);
                            correctImage.setVisibility(View.GONE);
                            gameQaQuestion.setText(question(questionCount));
                            gameQaQuestionNumber.setText("Q" + questionCount);
                            correctImage.setOnClickListener(null);
                            gameQaAnswer.setText("");
                        }
                    });
                } else {
                    incorrectImage.setVisibility(View.VISIBLE);
                    incorrectImage.animate()
                            .alpha(1.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                }
                            });
                    incorrectImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            incorrectImage.setAlpha(0.0f);
                            incorrectImage.setVisibility(View.GONE);
                            incorrectImage.setOnClickListener(null);
                        }
                    });
                }
            }

        }
    };
}

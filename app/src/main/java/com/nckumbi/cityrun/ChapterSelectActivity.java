package com.nckumbi.cityrun;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by DADA on 2016/6/14.
 * vincent8397@gmail.com
 */
public class ChapterSelectActivity extends AppCompatActivity {

    GifMovieView chapterSelectGif;
    TextView chapterSelectClock;
    ImageButton selectChapterGameStartImageButton;

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
        selectChapterGameStartImageButton = (ImageButton) findViewById(R.id.selectChapterGameStartImageButton);

        selectChapterGameStartImageButton.setOnClickListener(gameStartClicked);
    }

    protected View.OnClickListener gameStartClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}

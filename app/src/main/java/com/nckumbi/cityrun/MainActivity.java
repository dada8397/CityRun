package com.nckumbi.cityrun;

import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        final GifMovieView mainBikeGif = (GifMovieView) findViewById(R.id.mainBikeGif);
        mainBikeGif.setMovieResource(R.drawable.main_bike);
        final GifMovieView mainMountainGif = (GifMovieView) findViewById(R.id.mainMountainGif);
        mainMountainGif.setMovieResource(R.drawable.main_mountain);
    }
}

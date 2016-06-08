package com.nckumbi.cityrun;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DADA on 2016/6/7.
 * vincent8397@gmail.com
 */
public class RegisterActivity extends AppCompatActivity {

    EditText regUserNameEditText;
    EditText regEmailEditText;
    EditText regPasswordEditText;
    EditText regConfirmPasswordEditText;
    ImageButton regRegisterImageButton;
    ImageButton regBackImageButton;
    CheckBox policyCheckBox;

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
        setContentView(R.layout.activity_register);

        regUserNameEditText = (EditText) findViewById(R.id.regUserNameEditText);
        regEmailEditText = (EditText) findViewById(R.id.regEmailEditText);
        regPasswordEditText = (EditText) findViewById(R.id.regPasswordEditText);
        regConfirmPasswordEditText = (EditText) findViewById(R.id.regConfirmPasswordEditText);
        regRegisterImageButton = (ImageButton) findViewById(R.id.regRegisterImageButton);
        regBackImageButton = (ImageButton) findViewById(R.id.regBackImageButton);
        policyCheckBox = (CheckBox) findViewById(R.id.policyCheckBox);

        regBackImageButton.setOnClickListener(backImageButtonOnClick);
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
        if(MainActivity.stopped) {
            MainActivity.player = new BackgroundMusicPlayer(RegisterActivity.this, R.raw.main_bgm, true);
            MainActivity.player.execute();
            MainActivity.stopped = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected View.OnClickListener backImageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterActivity.this.finish();
        }
    };
}

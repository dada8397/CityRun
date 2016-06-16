package com.nckumbi.cityrun;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nckumbi.cityrun.utils.ProfileHelper;
import com.nckumbi.cityrun.utils.Utils;

import org.json.JSONObject;

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
        regRegisterImageButton.setOnClickListener(registerImageButtonOnClick);
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
            BackgroundMusicService.start();
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

    protected View.OnClickListener backImageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegisterActivity.this.finish();
        }
    };

    protected View.OnClickListener registerImageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (regUserNameEditText.getText().toString().isEmpty()
                    || regEmailEditText.getText().toString().isEmpty()
                    || regPasswordEditText.getText().toString().isEmpty()
                    || regConfirmPasswordEditText.getText().toString().isEmpty()) {
                Utils.showErrorDialog(RegisterActivity.this, "請輸入完整的註冊資訊！");
            } else if (!regPasswordEditText.getText().toString().equals(
                    regConfirmPasswordEditText.getText().toString())) {
                Utils.showErrorDialog(RegisterActivity.this, "兩次密碼不一致！");
            } else if (!policyCheckBox.isChecked()) {
                Utils.showErrorDialog(RegisterActivity.this, "您必須接受隱私權政策才能繼續使用！");
            } else {
                final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this,
                        "請稍後", "等待伺服器回應中...", true);

                ProfileHelper.register(regUserNameEditText.getText().toString(),
                        regEmailEditText.getText().toString(),
                        regPasswordEditText.getText().toString(),
                        new ProfileHelper.Callback() {
                            @Override
                            public void onComplete(JSONObject result) {
                                progressDialog.dismiss();

                                if (result == null) {
                                    Toast.makeText(RegisterActivity.this, R.string.msg_network_error,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        if (result.getBoolean("result")) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "註冊成功，即將返回登入頁面。", Toast.LENGTH_LONG).show();

                                            RegisterActivity.this.finish();
                                        } else {
                                            Utils.showErrorDialog(RegisterActivity.this,
                                                    R.string.msg_register_fail);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
            }
        }
    };
}

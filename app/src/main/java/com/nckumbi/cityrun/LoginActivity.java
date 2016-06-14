package com.nckumbi.cityrun;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nckumbi.cityrun.utils.ProfileHelper;
import com.nckumbi.cityrun.utils.Utils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DADA on 2016/6/7.
 * vincent8397@gmail.com
 */
public class LoginActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    ImageButton loginImageButton;
    TextView registerTextView;
    TextView forgetPasswordTextView;
    CheckBox rememberCheckBox;

    /* Facebook Login Manager */
    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
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
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginImageButton = (ImageButton) findViewById(R.id.loginImageButton);
        registerTextView = (TextView) findViewById(R.id.registerTextView);
        forgetPasswordTextView = (TextView) findViewById(R.id.forgetPasswordTextView);
        rememberCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox);

        loginImageButton.setOnClickListener(loginImageButtonOnClick);
        registerTextView.setOnClickListener(registerTextViewOnClick);
        forgetPasswordTextView.setOnClickListener(forgetPasswordTextViewOnClick);

        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        if (facebookLoginButton != null) {
            Log.d("Facebook", "Login Button Found");
            facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            facebookLoginButton.registerCallback(callbackManager, facebookCallback);
        }
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
            MainActivity.player = new BackgroundMusicPlayer(LoginActivity.this, R.raw.main_bgm, true);
            MainActivity.player.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            MainActivity.stopped = false;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected View.OnClickListener loginImageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (emailEditText.getText().toString().isEmpty()
                    || passwordEditText.getText().toString().isEmpty()) {
                Utils.showErrorDialog(LoginActivity.this, "請輸入完整的登入資訊！");
            } else {
                login(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        }
    };

    protected View.OnClickListener facebookLoginImageButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    protected View.OnClickListener registerTextViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    protected View.OnClickListener forgetPasswordTextViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("Facebook", "Login success");

            // Ask for personal information
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), graphCallback);

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.d("Facebook", "Login cancel");
        }

        @Override
        public void onError(FacebookException exception) {
            Log.d("Facebook", "Login error");
            Toast.makeText(LoginActivity.this, R.string.msg_network_error, Toast.LENGTH_LONG).show();
        }
    };

    private GraphRequest.GraphJSONObjectCallback graphCallback = new GraphRequest.GraphJSONObjectCallback() {
        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {
            Log.d("Facebook", object.optString("name"));
            Log.d("Facebook", object.optString("email"));

            final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this,
                    "請稍後", "等待伺服器回應中...", true);

            ProfileHelper.fbLogin(
                    object.optString("id"),
                    object.optString("name"),
                    object.optString("email"),
                    new ProfileHelper.Callback() {
                        @Override
                        public void onComplete(JSONObject result) {
                            progressDialog.dismiss();

                            if (result == null) {
                                Toast.makeText(LoginActivity.this, R.string.msg_network_error,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    if (result.getBoolean("result")) {
                                        if (LoginActivity.this.rememberCheckBox.isChecked()) {
                                            ProfileHelper.saveCurrentUuid(LoginActivity.this, result.getString("uuid"));
                                        }

                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, GameMenuActivity.class);
                                        intent.putExtras(Utils.JsonToBundle(result));
                                        startActivity(intent);

                                        LoginActivity.this.finish();
                                    } else {
                                        LoginManager.getInstance().logOut();
                                        Utils.showErrorDialog(LoginActivity.this, R.string.msg_login_fb_fail);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    });
        }
    };

    private void login(String email, String password) {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this,
                "請稍後", "等待伺服器回應中...", true);

        ProfileHelper.login(email, password, new ProfileHelper.Callback() {
            @Override
            public void onComplete(JSONObject result) {
                progressDialog.dismiss();

                if (result == null) {
                    Toast.makeText(LoginActivity.this, R.string.msg_network_error, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (result.getBoolean("result")) {
                            if (LoginActivity.this.rememberCheckBox.isChecked()) {
                                ProfileHelper.saveCurrentUuid(LoginActivity.this, result.getString("uuid"));
                            }

                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, GameMenuActivity.class);
                            intent.putExtras(Utils.JsonToBundle(result));
                            startActivity(intent);

                            LoginActivity.this.finish();
                        } else {
                            Utils.showErrorDialog(LoginActivity.this, R.string.msg_login_fail);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}

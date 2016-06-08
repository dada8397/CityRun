package com.nckumbi.cityrun.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nckumbi.cityrun.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/5/12.
 */
public class ProfileHelper {
    private static final String LOGIN_URL = "http://luffy.ee.ncku.edu.tw/~140454/SeekretMarket/Server/login.php";
    private static final String REGISTER_URL = "http://luffy.ee.ncku.edu.tw/~140454/SeekretMarket/Server/register.php";
    private static final String QUERY_URL = "http://luffy.ee.ncku.edu.tw/~140454/SeekretMarket/Server/query.php";
    private static final String UPDATE_URL = "http://luffy.ee.ncku.edu.tw/~140454/SeekretMarket/Server/update.php";

    public static final String PREF_CURRENT_UUID = "uuid";

    public static String getCurrentUuid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        return preferences.getString(PREF_CURRENT_UUID, null);
    }

    public static void saveCurrentUuid(Context context, String uuid) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        preferences.edit().putString(PREF_CURRENT_UUID, uuid).apply();
    }

    public static void login(String email, String password, Callback callback) {
        new LoginTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email, password);
    }

    public static void fbLogin(final String id, final String name, final String email, final Callback callback) {
        final Callback registerCallback = new Callback() {
            @Override
            public void onComplete(JSONObject result) {
                new LoginTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email, id);
            }
        };

        Callback loginCallback = new Callback() {
            @Override
            public void onComplete(JSONObject result) {
                try {
                    if (result != null && !result.getBoolean("result")) {
                        new RegisterTask(registerCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, email, id);
                    } else {
                        callback.onComplete(result);
                    }
                } catch (Exception e) {
                }
            }
        };

        new LoginTask(loginCallback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email, id);
    }

    public static void register(String name, String email, String password, Callback callback) {
        new RegisterTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, email, password);
    }

    public static void query(String uuid, Callback callback) {
        new QueryTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uuid);
    }

    public interface Callback {
        void onComplete(JSONObject result);
    }

    private static class QueryTask extends AsyncTask<Object, Void, JSONObject> {
        private Callback callback;

        QueryTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("uuid", (String) params[0]));

            String response = Utils.sendPostRequest(QUERY_URL, postParams);
            JSONObject result = null;

            try {
                result = new JSONObject(response);
            } catch (Exception e) {
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (callback != null) {
                callback.onComplete(result);
            }
        }
    }

    private static class RegisterTask extends AsyncTask<Object, Void, JSONObject> {
        private Callback callback;

        RegisterTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("name", (String) params[0]));
            postParams.add(new BasicNameValuePair("email", (String) params[1]));
            postParams.add(new BasicNameValuePair("password", (String) params[2]));

            String response = Utils.sendPostRequest(REGISTER_URL, postParams);
            JSONObject result = null;

            try {
                result = new JSONObject(response);
            } catch (Exception e) {
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (callback != null) {
                callback.onComplete(result);
            }
        }
    }

    private static class LoginTask extends AsyncTask<Object, Void, JSONObject> {
        private Callback callback;

        LoginTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("email", (String) params[0]));
            postParams.add(new BasicNameValuePair("password", (String) params[1]));

            String response = Utils.sendPostRequest(LOGIN_URL, postParams);
            JSONObject result = null;

            try {
                result = new JSONObject(response);
            } catch (Exception e) {
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (callback != null) {
                callback.onComplete(result);
            }
        }
    }
}
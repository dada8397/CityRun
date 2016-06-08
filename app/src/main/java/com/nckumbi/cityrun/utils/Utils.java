package com.nckumbi.cityrun.utils;

import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2016/5/12.
 */
public class Utils {
    public static String sendPostRequest(String url, List<NameValuePair> params) {
        // Setup a POST request
        HttpPost httpRequest = new HttpPost(url);

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            Log.d("HttpPost", "Response Code: " + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // Get response content
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());

            return null;
        }

        return null;
    }

    public static Bundle JsonToBundle(JSONObject jsonObject) {
        Bundle bundle = new Bundle();
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();

            if (jsonObject.isNull(key)) {
                bundle.putString(key, null);
                continue;
            }

            Object value = jsonObject.opt(key);
            if (value instanceof JSONObject) {
                bundle.putBundle(key, JsonToBundle((JSONObject) value));
            } else if (value instanceof JSONArray) {
                bundle.putParcelableArrayList(key, JsonToBundle((JSONArray) value));
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (boolean) value);
            } else if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (int) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (long) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (double) value);
            }
        }

        return bundle;
    }

    public static ArrayList<Bundle> JsonToBundle(final JSONArray array) {
        ArrayList<Bundle> bundles = new ArrayList<>();

        for (int i = 0, size = array.length(); i < size; ++i) {
            bundles.add(JsonToBundle(array.optJSONObject(i)));
        }

        return bundles;
    }
}

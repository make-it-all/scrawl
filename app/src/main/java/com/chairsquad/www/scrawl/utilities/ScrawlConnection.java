package com.chairsquad.www.scrawl.utilities;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by henry on 13/04/17.
 */

public class ScrawlConnection {

    public static final int API_VERSION = 1;
//    public static final String BASE_URL = "http://192.168.0.15:9292/";
    public static final String BASE_URL = "http://scrawl-app-demo.herokuapp.com/";
    public static final String API_URL = BASE_URL + "api/v"+API_VERSION+"/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private final static String TOKEN_KEY = "token";
    private final static String EMAIL_KEY = "email";

    public static String getAbsoluteUrl(String path) {
        return API_URL + path;
    }

    public static JSONObject get(Context context, String path, RequestParams params) {
        try {
            URL url = new URL(API_URL + path);
            if (params == null) {
                params = getParamsWithToken(context);
            } else {
                params.add("token", PreferenceUtils.getToken(context));
            }
            return NetworkUtils.get(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject post(Context context, String path, RequestParams params) {
        try {
            URL url = new URL(API_URL + path);
            if (params == null) {
                params = getParamsWithToken(context);
            } else {
                params.add("token", PreferenceUtils.getToken(context));
            }
            return NetworkUtils.post(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestParams getParamsWithToken(Context context) {
        RequestParams params = new RequestParams();
        params.add("token", PreferenceUtils.getToken(context));
        return params;
    }


    public static boolean isLoggedIn(Context context) {
        final String token = PreferenceUtils.getToken(context);
        final String email = PreferenceUtils.getEmail(context);

        return !(token == null || email == null || TextUtils.isEmpty(email));
    }

    private static boolean isTokenValid(Context context) {
        String token = PreferenceUtils.getToken(context);
        try {
            JSONObject result = get(context, "ping", null);
            String email = result.getString("user");
            if (email.equals(PreferenceUtils.getEmail(context))) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean login(Context context, String email, String password) {
        boolean success = false;
        try {
            RequestParams params = new RequestParams();
            params.add("email", email);
            params.add("password", password);
            JSONObject result = NetworkUtils.post(API_URL + "auth/sign_in", params);
            if (result.has("token")) {
                success = true;
                String token = result.getString("token");
                PreferenceUtils.setAuthDetails(context, email, token);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    public static void logout(Context context) {
        PreferenceUtils.clearAuthDetails(context);
    }

}

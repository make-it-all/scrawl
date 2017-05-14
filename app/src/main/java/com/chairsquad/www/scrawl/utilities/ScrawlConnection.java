package com.chairsquad.www.scrawl.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by henry on 13/04/17.
 */

public class ScrawlConnection {

    private final static String TOKEN_KEY = "token";
    private final static String EMAIL_KEY = "email";

    public final static Uri BASE_URL = Uri.parse("192.168.1.140:3000");

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public ScrawlConnection(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLoggedIn() {
        String token = mSharedPreferences.getString(TOKEN_KEY, null);
        String email = mSharedPreferences.getString(EMAIL_KEY, null);
        return token!=null && email!=null;
    }

    public boolean login(String email, String password) {
        //TODO: make this check email and password are correct
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(EMAIL_KEY, email);
        editor.putString(TOKEN_KEY, "token");
        editor.commit();
        return true;
    }

}

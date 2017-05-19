package com.chairsquad.www.scrawl.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by henry on 18/05/17.
 */

public class PreferenceUtils {

    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_KEY = "token";
    private static final String WIFI_ONLY_KEY = "pref_wifi_key";
    private static final String LAST_SYNC_KEY = "last_sync";


    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(EMAIL_KEY, null);
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(TOKEN_KEY, null);
    }

    public static void setAuthDetails(Context context, String email, String token) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit();
        editor.putString(EMAIL_KEY, email);
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public static void clearAuthDetails(Context context) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit();
        editor.remove(EMAIL_KEY);
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    public static boolean getWifiOnly(Context context) {
        SharedPreferences prefs = getUserPreferences(context);
        return prefs.getBoolean(WIFI_ONLY_KEY, true);
    }
    
    public static long getLastSyncedAt(Context context) {
        SharedPreferences prefs = getUserPreferences(context);
        return prefs.getLong(LAST_SYNC_KEY,-1);
    }

    public static void setLastSyncedAt(Context context, long time) {
        SharedPreferences.Editor editor = getUserPreferences(context).edit();
        editor.putLong(LAST_SYNC_KEY, time);
        editor.apply();
    }

    private static SharedPreferences getUserPreferences(Context context) {
        return context.getSharedPreferences(getEmail(context), Context.MODE_PRIVATE);
    }
}

package com.chairsquad.www.scrawl.utilities;

import android.text.Html;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by henry on 18/05/17.
 */

public class NoteUtils {

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String stripHTML(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(text).toString();
        }
    }

    public static String timeAgoInWords(long timestamp) {
        long now = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();

        long diff = now - timestamp;

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 day";
        } else {
            return diff / DAY_MILLIS + " day";
        }
    }

}

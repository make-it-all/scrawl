package com.chairsquad.www.scrawl.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by henry on 18/04/17.
 */

public class NotesContract {

    public static final String CONTENT_AUTHORITY = "com.chairsquad.www.scrawl";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTES = "notes";

    public static class NoteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTES)
                .build();
        public static String TABLE_NAME = "notes";
        public static String COLUMN_NAME = "name";
        public static String COLUMN_BODY = "body";
        public static String COLUMN_IS_DELETED = "is_deleted";
        public static String COLUMN_CREATED_AT = "created_at";

        public static Uri buildUriForId(int id) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
    }

}

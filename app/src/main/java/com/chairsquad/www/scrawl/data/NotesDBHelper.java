package com.chairsquad.www.scrawl.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;
import com.chairsquad.www.scrawl.utilities.PreferenceUtils;

/**
 * Created by henry on 18/04/17.
 */

public class NotesDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    public NotesDBHelper(Context context) {
        super(context, PreferenceUtils.getEmail(context) + "_notes.db", null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("HIHI", "CREATING");
        final String SQL_CREATE_TABLE_NOTES = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_REMOTE_ID + " INTEGER NOT NULL DEFAULT -1, " +
                NoteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_BODY + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_IS_STARRED + " BOOLEAN NOT NULL DEFAULT FALSE, " +
                NoteEntry.COLUMN_IS_DELETED + " BOOLEAN NOT NULL DEFAULT FALSE, " +
                NoteEntry.COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                NoteEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB TAG", String.valueOf(oldVersion));
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + NoteEntry.TABLE_NAME +
                        " ADD " + NoteEntry.COLUMN_IS_DELETED  + " BOOLEAN NOT NULL DEFAULT FALSE");
            case 2:
                db.execSQL("ALTER TABLE " + NoteEntry.TABLE_NAME +
                        " ADD " + NoteEntry.COLUMN_REMOTE_ID + " INTEGER DEFAULT -1");
                db.execSQL("ALTER TABLE " + NoteEntry.TABLE_NAME +
                        " ADD " + NoteEntry.COLUMN_UPDATED_AT + "  TIMESTAMP");
                db.execSQL("ALTER TABLE " + NoteEntry.TABLE_NAME +
                        " ADD " + NoteEntry.COLUMN_IS_STARRED + " BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }
}

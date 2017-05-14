package com.chairsquad.www.scrawl.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;

/**
 * Created by henry on 18/04/17.
 */

public class NotesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";

    private static final int DATABASE_VERSION = 2;

    public NotesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_NOTES = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_BODY + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
                onCreate(db);
            case 1:
                db.execSQL("ALTER TABLE " + NoteEntry.TABLE_NAME +
                        " ADD " + NoteEntry.COLUMN_IS_DELETED  + " BOOLEAN NOT NULL DEFAULT FALSE");

        }
    }
}

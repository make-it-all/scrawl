package com.chairsquad.www.scrawl.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;

/**
 * Created by henry on 18/04/17.
 */

public class NotesContentProvider extends ContentProvider {

    public static final int NOTES = 100;
    public static final int NOTE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NotesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, NotesContract.PATH_NOTES, NOTES);
        matcher.addURI(authority, NotesContract.PATH_NOTES+"/#", NOTE_WITH_ID);
        return matcher;
    }

    private NotesDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new NotesDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                if (selection != null && selection.equals("ALL")) {
                    cursor = mOpenHelper.getReadableDatabase().query(
                            NoteEntry.TABLE_NAME,
                            projection,
                            null,
                            null,
                            null,
                            null,
                            sortOrder);
                } else {
                    cursor = mOpenHelper.getReadableDatabase().query(
                        NoteEntry.TABLE_NAME,
                        projection,
                        NoteEntry.COLUMN_IS_DELETED + "<>?",
                        new String[] { "1" },
                        null,
                        null,
                        sortOrder);
                }
                break;
            case NOTE_WITH_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        NoteEntry.TABLE_NAME,
                        projection,
                        NoteEntry._ID + "=?",
                        new String[]{ uri.getLastPathSegment() },
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newUri;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                int id = (int) mOpenHelper.getWritableDatabase().insert(
                        NoteEntry.TABLE_NAME,
                        null,
                        values
                );
                newUri = NoteEntry.buildUriForId(id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("SCRAWL", "NEW NOTE JUST SAVED, URI IS NOW: " + newUri.toString());
        return newUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = 0;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                values.put(NoteEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        NoteEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case NOTE_WITH_ID:
                values.put(NoteEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        NoteEntry.TABLE_NAME,
                        values,
                        NoteEntry._ID + "=?",
                        new String[] { uri.getLastPathSegment() }
                        );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTE_WITH_ID:
                ContentValues values = new ContentValues();
                values.put(NoteEntry.COLUMN_IS_DELETED, true);
                values.put(NoteEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
                rowsDeleted = mOpenHelper.getWritableDatabase().update(
                        NoteEntry.TABLE_NAME,
                        values,
                        NoteEntry._ID + "=?",
                        new String[] { uri.getLastPathSegment() }
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}

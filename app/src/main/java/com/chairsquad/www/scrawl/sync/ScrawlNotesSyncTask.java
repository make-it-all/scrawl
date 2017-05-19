package com.chairsquad.www.scrawl.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.chairsquad.www.scrawl.utilities.PreferenceUtils;
import com.chairsquad.www.scrawl.utilities.ScrawlConnection;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;

/**
 * Created by henry on 18/05/17.
 */

public class ScrawlNotesSyncTask {

    public interface NotesUpdated {
        public void onNotesSyncUpdated();
    }

    synchronized public static void execute(Context context) {
        //get last synced time.
        long last_sync = PreferenceUtils.getLastSyncedAt(context);
        // update sync time incase note updated between now and network requests finish
        PreferenceUtils.setLastSyncedAt(context, System.currentTimeMillis());

        // Get recently updated notes
        Uri notesUri = NoteEntry.CONTENT_URI;
        String selectionStatement = NoteEntry.getSelectionForUpdatedSince(last_sync);
        Cursor cursor = context.getContentResolver().query(
                notesUri,
                null,
                "ALL",
                null,
                null);

        // transform updated notes into paramaters to send to server
        JSONObject jsonObject = new JSONObject();
        while (cursor.moveToNext()) {
            JSONObject j = new JSONObject();
            try {
                j.put("name", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NAME)));
                j.put("remote_id", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_REMOTE_ID)));
                j.put("body", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_BODY)).replace("&", "%26"));
                j.put("is_deleted", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_IS_DELETED)));
                j.put("is_starred", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_IS_STARRED)));
                j.put("updated_at", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_UPDATED_AT)));
                j.put("created_at", cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_CREATED_AT)));


                int id = cursor.getInt(cursor.getColumnIndex(NoteEntry._ID));
                jsonObject.put(id+"", j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put("notes", jsonObject);
        // Push up and pull down
        JSONObject response = ScrawlConnection.post(context, "notes/sync", requestParams);
        Log.d("PUSHED UP", response.toString());
        try {

            // Update all remote ids to match ones sent back
            JSONObject new_notes = response.getJSONObject("new_notes");
            Iterator<String> iter = new_notes.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = new_notes.getString(key);

                    ContentValues values = new ContentValues();
                    values.put(NoteEntry.COLUMN_REMOTE_ID, value);
                    context.getContentResolver().update(
                            NoteEntry.buildUriForId(Integer.valueOf(key)),
                            values, null, null
                    );
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }

            JSONArray notes = response.getJSONArray("notes");
            for(int i=0;i<notes.length();i++){
                JSONObject note = notes.getJSONObject(i);
                String remote_id = note.getString("id");
                String name = note.getString("name");
                String body = note.getString("body");
                boolean is_deleted = note.getBoolean("is_deleted");
                boolean is_starred = note.getBoolean("is_starred");

                ContentValues values = new ContentValues();
                values.put(NoteEntry.COLUMN_NAME, name);
                values.put(NoteEntry.COLUMN_BODY, body);
                values.put(NoteEntry.COLUMN_IS_DELETED, is_deleted);
                values.put(NoteEntry.COLUMN_IS_STARRED, is_starred);

                context.getContentResolver().update(
                        NoteEntry.CONTENT_URI,
                        values,
                        NoteEntry.COLUMN_REMOTE_ID + "=?",
                        new String[]{remote_id}
                );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent i = new Intent("NOTES_UPDATED");
        context.sendBroadcast(i);

    }

}

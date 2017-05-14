package com.chairsquad.www.scrawl.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chairsquad.www.scrawl.NoteAdapter;
import com.chairsquad.www.scrawl.R;
import com.chairsquad.www.scrawl.data.NotesContract;

public class AllNotesActivity extends AppCompatActivity implements
        NoteAdapter.NoteAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ALL_NOTES_LOADER_ID = 1337;
    private static final int NOTE_ACTIVITY_REQUEST = 69;

    private RecyclerView mRecyclerView;
    private NoteAdapter mNoteAdapter;

    private CoordinatorLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContainer = (CoordinatorLayout) findViewById(R.id.container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_notes);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //divider lines
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setHasFixedSize(true);

        //creating and setting the adapter
        mNoteAdapter = new NoteAdapter(this);
        mNoteAdapter.setOnClickHandler(this);

        mRecyclerView.setAdapter(mNoteAdapter);

        getSupportLoaderManager().restartLoader(ALL_NOTES_LOADER_ID, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewNoteActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ALL_NOTES_LOADER_ID, null, this);
    }

    private void openNewNoteActivity() {
        Intent intent = new Intent(AllNotesActivity.this, NoteActivity.class);
        startActivityForResult(intent, NOTE_ACTIVITY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) return;
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SCRAWL", "RESULT RECIEVED");
        Log.d("SCRAWL", "REQUESTED WITH: " + requestCode);
        Log.d("SCRAWL", "RESULTED IN: " + resultCode);
//        Log.d("SCRAWL", "DATA: " + data.getData());
        switch (requestCode) {
            case NOTE_ACTIVITY_REQUEST:
                final Uri deletedUri = data.getData();
                if (deletedUri != null) {
                    Snackbar.make(mContainer, "Note Deleted!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues values = new ContentValues();
                            values.put(NotesContract.NoteEntry.COLUMN_IS_DELETED, false);
                            getContentResolver().update(deletedUri, values, null, null);
                            getSupportLoaderManager().restartLoader(ALL_NOTES_LOADER_ID, null, AllNotesActivity.this);
                        }
                    }).show();
                }
                break;
        }
    }

    @Override
    public void onNoteClick(int noteId) {
        Intent intent = new Intent(AllNotesActivity.this, NoteActivity.class);
        intent.setData(NotesContract.NoteEntry.buildUriForId(noteId));
        startActivityForResult(intent, NOTE_ACTIVITY_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ALL_NOTES_LOADER_ID) {
            Uri uri = NotesContract.NoteEntry.CONTENT_URI;
            String sortOrder = NotesContract.NoteEntry.COLUMN_CREATED_AT + " ASC";

            return new CursorLoader(this, uri, null, null, null, sortOrder);
        } else {
            throw new UnsupportedOperationException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNoteAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNoteAdapter.setCursor(null);
    }
}

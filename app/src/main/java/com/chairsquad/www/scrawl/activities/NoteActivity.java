package com.chairsquad.www.scrawl.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chairsquad.www.scrawl.R;
import com.chairsquad.www.scrawl.data.NotesContract;
import com.chairsquad.www.scrawl.utilities.Wysiwyg;
import com.facebook.stetho.Stetho;

import static com.chairsquad.www.scrawl.R.id.toolbar;

public class NoteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        Wysiwyg.OnToolChangeListener {

    private static final int NOTE_LOADER_ID = 34;

    private Uri mUri;

    private TextView mTitleTextView;
    private Toolbar mActionBarToolbar;

    private Toolbar mTopEditingTools;
    private Toolbar mBottomEditingTools;

    private Wysiwyg mBodyWysiwyg;

    private FloatingActionButton mFab;

    private boolean mFullscreen = false;
    private boolean mEditing = false;
    private boolean mNewNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Stetho.initializeWithDefaults(this);

        mActionBarToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        //handle title editing
        mTitleTextView = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditingTitle();
            }
        });

        //Load note data
        mUri = getIntent().getData();
        if (mUri == null) {
            mNewNote = true;
        } else {
            getSupportLoaderManager().initLoader(NOTE_LOADER_ID, null, this);
        }

        //Floating Action Button Stuff
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginEditing();
            }
        });


        //wysiwyg
        mBodyWysiwyg = (Wysiwyg) findViewById(R.id.wys_note_body);
        mBodyWysiwyg.setOnToolChangeListener(this);

        //initialize tools
        mTopEditingTools = (Toolbar) findViewById(R.id.tb_top_editing_tools);
        mBottomEditingTools = (Toolbar) findViewById(R.id.tb_bottom_editing_tools);

        mTopEditingTools.inflateMenu(R.menu.wysiwyg_top_tools);
        mBottomEditingTools.inflateMenu(R.menu.wysiwyg_bottom_tools);

        mTopEditingTools.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onToolPressed(item);
            }
        });
        mBottomEditingTools.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onToolPressed(item);
            }
        });

        if (mNewNote) {
            beginEditing();
        }

    }

    private void startEditingTitle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Note");

        final EditText input = new EditText(this);
        input.setText(mTitleTextView.getText());
        input.setSelectAllOnFocus(true);
        input.setSingleLine(true);

        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
               mTitleTextView.setText(input.getText());
               input.setEnabled(false);
                save();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
               input.setEnabled(false);
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        // Initially disable the button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Now set the textchange listener for edittext
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(count>0);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void beginEditing() {
        mEditing = true;
        mFab.hide();
        mBodyWysiwyg.setEnabled(true);
        mBodyWysiwyg.focus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mBodyWysiwyg, InputMethodManager.SHOW_IMPLICIT);

        getSupportActionBar().hide();
        mTopEditingTools.setVisibility(View.VISIBLE);
        mBottomEditingTools.setVisibility(View.VISIBLE);

    }

    private void finishEditing() {
        mEditing = false;
        mFab.show();
        mBodyWysiwyg.setEnabled(false);

        getSupportActionBar().show();
        mTopEditingTools.setVisibility(View.GONE);
        mBottomEditingTools.setVisibility(View.GONE);

        save();
    }

    private boolean onToolPressed(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.tools_save:
                finishEditing();
                break;
            case R.id.tool_bold:
                mBodyWysiwyg.setBold();
                break;
            case R.id.tool_italics:
                mBodyWysiwyg.setItalic();
                break;
            case R.id.tool_underline:
                mBodyWysiwyg.setUnderline();
                break;
            case R.id.tool_strikethrough:
                mBodyWysiwyg.setStrikethough();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onToolChange(String tool, boolean enabled) {
        Log.d("SCRAWL", "Tool "+ tool + " changed to " + String.valueOf(enabled));
        if (tool.equals("bold")) {
            View view = mBottomEditingTools.findViewById(R.id.tool_bold);
            setToolSelected(view, enabled);
        } else if (tool.equals("italic")) {
            View view = mBottomEditingTools.findViewById(R.id.tool_italics);
            setToolSelected(view, enabled);
        } else if (tool.equals("underline")) {
            View view = mBottomEditingTools.findViewById(R.id.tool_underline);
            setToolSelected(view, enabled);
        } else if (tool.equals("strikethrough")) {
            View view = mBottomEditingTools.findViewById(R.id.tool_strikethrough);
            setToolSelected(view, enabled);
        }
    }

    public void setToolSelected(final View tool, final boolean enabled) {
        if (enabled) {
            tool.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            tool.setBackgroundColor(0);
        }
    }

    public void save() {
        ContentValues values = new ContentValues();
        values.put(NotesContract.NoteEntry.COLUMN_NAME, mTitleTextView.getText().toString());
        values.put(NotesContract.NoteEntry.COLUMN_BODY, mBodyWysiwyg.getHtml());
        Log.d("SCRAWL", mBodyWysiwyg.getHtml());
        if (mNewNote) {
            mUri = getContentResolver().insert(NotesContract.NoteEntry.CONTENT_URI, values);
            mNewNote = false;
        } else {
            getContentResolver().update(mUri, values, null, null);
        }
    }

    public void delete() {
        if (!mNewNote) {
            getContentResolver().delete(mUri, null, null);
            Intent resultIntent = new Intent();
            resultIntent.setData(mUri);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }

    //Normal menu stuff

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_share) {
            Intent intent = createShareIntent();
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            delete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText("Sharing a note!")
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }


    //Loader callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == NOTE_LOADER_ID) {
            return new CursorLoader(this, mUri, null, null, null, null);
        } else {
            throw new UnsupportedOperationException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //No data
        if (data == null || !data.moveToFirst()) return;

        String name = data.getString(data.getColumnIndex(NotesContract.NoteEntry.COLUMN_NAME));
        String body = data.getString(data.getColumnIndex(NotesContract.NoteEntry.COLUMN_BODY));

        mTitleTextView.setText(name);
        mBodyWysiwyg.setHtml(body);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

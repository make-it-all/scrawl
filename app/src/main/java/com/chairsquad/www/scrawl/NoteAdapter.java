package com.chairsquad.www.scrawl;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;

/**
 * Created by henry on 18/04/17.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private NoteAdapterOnClickHandler mClickHandler;

    public interface  NoteAdapterOnClickHandler {
        void onNoteClick(int noteId);
    }

    public NoteAdapter(Context context) {
        mContext = context;
    }

    public void setOnClickHandler(NoteAdapterOnClickHandler handler) {
        mClickHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout = R.layout.note_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layout, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String name = mCursor.getString(mCursor.getColumnIndex(NoteEntry.COLUMN_NAME));
        String body = mCursor.getString(mCursor.getColumnIndex(NoteEntry.COLUMN_BODY));
        int id = mCursor.getInt(mCursor.getColumnIndex(NoteEntry._ID));

        holder.mNoteTextView.setText(name);
        holder.setId(id);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null)
            notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mNoteTextView;
        private int mId;

        public ViewHolder(View view) {
            super(view);
            mNoteTextView = (TextView) view.findViewById(R.id.tv_note_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickHandler != null) {
                mClickHandler.onNoteClick(mId);
            }
        }

        public void setId(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

}

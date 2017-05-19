package com.chairsquad.www.scrawl;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chairsquad.www.scrawl.data.NotesContract.NoteEntry;
import com.chairsquad.www.scrawl.utilities.NoteUtils;

/**
 * Created by henry on 18/04/17.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private NoteAdapterOnClickHandler mClickHandler;
    private NoteAdapterOnLongClickHandler mLongClickHandler;

    public interface  NoteAdapterOnClickHandler {
        void onNoteClick(int noteId);
    }

    public interface NoteAdapterOnLongClickHandler {
        void onNoteLongClick(int noteId, boolean state);
    }


    public NoteAdapter(Context context) {
        mContext = context;
    }

    public void setOnClickHandler(NoteAdapterOnClickHandler handler) {
        mClickHandler = handler;
    }
    public void setOnLongClickHandler(NoteAdapterOnLongClickHandler handler) {
        mLongClickHandler = handler;
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
        boolean starred = mCursor.getInt(mCursor.getColumnIndex(NoteEntry.COLUMN_IS_STARRED)) > 0;
        String updatedAt = mCursor.getString(mCursor.getColumnIndex(NoteEntry.COLUMN_UPDATED_AT));
        int id = mCursor.getInt(mCursor.getColumnIndex(NoteEntry._ID));


        holder.mNoteTextView.setText(name);
        holder.mNoteBodyTextView.setText(NoteUtils.stripHTML(body));
        holder.mNoteUpdatedView.setText(NoteUtils.timeAgoInWords(Long.valueOf(updatedAt)));
        holder.setStarred(starred);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public final TextView mNoteTextView;
        public final TextView mNoteBodyTextView;
        public final TextView mNoteUpdatedView;
        public final ImageView mNoteStarredView;

        private int mId;
        private boolean mStarred = false;

        public ViewHolder(View view) {
            super(view);
            mNoteTextView = (TextView) view.findViewById(R.id.tv_note_title);
            mNoteBodyTextView = (TextView) view.findViewById(R.id.tv_note_body);
            mNoteUpdatedView = (TextView) view.findViewById(R.id.tv_updated_time_ago);
            mNoteStarredView = (ImageView) view.findViewById(R.id.iv_note_starred);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
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

        public void setStarred(boolean state) {
            mStarred = state;
            if (mStarred) {
                mNoteStarredView.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                mNoteStarredView.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        }

        public int getId() {
            return mId;
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickHandler != null) {
                mLongClickHandler.onNoteLongClick(mId, mStarred);
            }
            return true;
        }
    }

}

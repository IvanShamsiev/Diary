package com.example.diary.ui.notes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Note;

import java.util.Collections;
import java.util.List;

import static com.example.diary.DiaryApp.dateFormat;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteClickListener onNoteClickListener;
    static final String INTENT_NOTE_ID = "noteId";


    NotesAdapter(OnNoteClickListener listener) {
        onNoteClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note,
                parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        noteViewHolder.bind(notes.get(i));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    void setNotes(List<Note> notes) {
        this.notes = notes;
        Collections.sort(this.notes, (n1, n2) ->
                n2.getLastChangeTime().compareTo(n1.getLastChangeTime()));
        notifyDataSetChanged();
    }

    void dataUpdate() {
        DiaryDao.updateNotes();
        setNotes(DiaryDao.getAllNotes());
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView twText;
        TextView twLastChangeTime;

        long noteId;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            twText = itemView.findViewById(R.id.listItemNote_twText);
            twLastChangeTime = itemView.findViewById(R.id.listItemNote_twLastChangeTime);
            itemView.setOnClickListener(v -> onNoteClickListener.onClick(notes.get(getLayoutPosition())));
            itemView.setOnCreateContextMenuListener(this);
        }

        void bind(Note note) {
            noteId = note.getId();
            twText.setText(note.getText());
            twLastChangeTime.setText(dateFormat.format(note.getLastChangeTime()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo cmi) {
            MenuItem item = menu.add(v.getContext().getString(R.string.delete_note_menu_item));
            item.setIntent(new Intent().putExtra(INTENT_NOTE_ID, noteId));
        }
    }

    static long getNoteIdFromIntent(Intent intent) {
        return intent.getLongExtra(INTENT_NOTE_ID, Note.NONE);
    }

    interface OnNoteClickListener {
        void onClick(Note note);
    }
}

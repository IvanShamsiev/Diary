package com.example.diary.notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.mm.yyyy HH:mm", Locale.getDefault());

    private List<Note> notes;

    private OnNoteClickListener noteClickListener;

    NotesAdapter(OnNoteClickListener listener) {
        noteClickListener = listener;
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
        Collections.sort(this.notes, (note, t1) ->
                t1.getLastChangeTime().compareTo(note.getLastChangeTime()));
        notifyDataSetChanged();
    }

    void dataUpdate() {
        DbManager.updateNotes();
        setNotes(DbManager.getAllNotes());
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView twText;
        TextView twLastChangeTime;

        int noteId;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            twText = itemView.findViewById(R.id.listItemNote_twText);
            twLastChangeTime = itemView.findViewById(R.id.listItemNote_twLastChangeTime);
            itemView.setOnClickListener(v -> noteClickListener.onClick(notes.get(getLayoutPosition())));
            itemView.setOnCreateContextMenuListener(this);
        }

        void bind(Note note) {
            noteId = note.getId();
            twText.setText(note.getText());
            twLastChangeTime.setText(dateFormat.format(note.getLastChangeTime()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo cmi) {
            menu.add(0, noteId, 0, "Удалить заметку");
        }
    }

    interface OnNoteClickListener {
        void onClick(Note note);
    }
}

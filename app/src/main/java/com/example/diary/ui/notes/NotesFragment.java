package com.example.diary.ui.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Note;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    public static final int NOTE_DETAILS_CODE = 0;

    NotesAdapter adapter;

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Set fab
        FloatingActionButton fab = view.findViewById(R.id.fabNotes);
        fab.setOnClickListener(btn -> createNewNote());

        // Set adapter
        adapter = new NotesAdapter(this::editNote);
        adapter.setNotes(new ArrayList<>(DiaryDao.getAllNotes()));

        // Set recycler view
        RecyclerView notesRecyclerView = view.findViewById(R.id.recyclerViewNotes);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        notesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == NOTE_DETAILS_CODE) adapter.dataUpdate();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = NotesAdapter.getNoteIdFromIntent(item.getIntent());
        if (item.getTitle().equals(getString(R.string.delete_note_menu_item))) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_note_question)
                    .setMessage(R.string.delete_note_msg)
                    .setNegativeButton(R.string.no, (di, i) -> di.cancel())
                    .setPositiveButton(R.string.yes, (di, i) -> {
                        DiaryDao.deleteNote(id);
                        adapter.dataUpdate();
                    })
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNewNote() {
        Note note = Note.getEmptyNote();
        editNote(note);
    }

    private void editNote(Note note) {
        Intent intent = NoteDetailsActivity.getIntent(getContext(), note.getId());
        startActivityForResult(intent, NOTE_DETAILS_CODE);
    }
}
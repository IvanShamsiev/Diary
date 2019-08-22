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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Note;

import java.util.ArrayList;

import static com.example.diary.ui.MainActivity.LOG_TAG;

public class NotesFragment extends Fragment {

    NotesAdapter adapter;

    public static final int NOTE_DETAILS_CODE = 0;

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
        adapter = new NotesAdapter(note -> startActivityForResult(
                NoteDetailsActivity.getIntent(getContext(), note.getId()), NOTE_DETAILS_CODE));
        adapter.setNotes(new ArrayList<>(DiaryDao.getAllNotes()));

        // Set recycler view
        RecyclerView notesListView = view.findViewById(R.id.recyclerViewNotes);
        notesListView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        notesListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == NOTE_DETAILS_CODE) adapter.dataUpdate();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = item.getIntent().getLongExtra("noteId", -1);
        if (item.getTitle().equals("Удалить заметку")) {
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
        Log.d(LOG_TAG, "ID: " + item.getItemId());
        return super.onContextItemSelected(item);
    }

    private void createNewNote() {
        Note note = new Note(-1, "", System.currentTimeMillis());
        editNote(note);
    }

    private void editNote(Note note) {
        Intent intent = new Intent(getContext(), NoteDetailsActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivityForResult(intent, NOTE_DETAILS_CODE);
    }
}
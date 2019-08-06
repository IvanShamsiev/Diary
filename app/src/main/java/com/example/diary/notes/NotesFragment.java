package com.example.diary.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.example.diary.R;
import com.example.diary.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.diary.MainActivity.LOG_TAG;

public class NotesFragment extends Fragment {

    RecyclerView notesListView;
    NotesAdapter adapter;

    public static final int NOTE_DETAILS_CODE = 0;
    public static final int NOTE_CHANGED_CODE = 1;

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

        FloatingActionButton fab = view.findViewById(R.id.fabNotes);
        fab.setOnClickListener(btn -> createNewNote());

        notesListView = view.findViewById(R.id.recyclerViewNotes);
        notesListView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter = new NotesAdapter(this::editNote);
        adapter.setNotes(new ArrayList<>(DbManager.getAllNotes()));
        notesListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NOTE_CHANGED_CODE)
        adapter.dataUpdate();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Удалить заметку")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Удалить заметку")
                    .setMessage("Вы действительно хотите удалить заметку?")
                    .setNegativeButton("Нет", (di, i) -> di.cancel())
                    .setPositiveButton("Да", (di, i) -> {
                        DbManager.deleteNote(item.getItemId());
                        adapter.dataUpdate();
                    })
                    .show();
            return true;
        }
        Log.d(LOG_TAG, "ID: " + item.getItemId());
        return super.onContextItemSelected(item);
    }

    private void createNewNote() {
        Note note = new Note(-1, "", new Date(System.currentTimeMillis()));
        editNote(note);
    }

    private void editNote(Note note) {
        Intent intent = new Intent(getContext(), NoteDetailsActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivityForResult(intent, NOTE_DETAILS_CODE);
    }
}
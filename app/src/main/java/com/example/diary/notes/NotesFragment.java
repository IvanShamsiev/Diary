package com.example.diary.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.diary.R;
import com.example.diary.db.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment {

    Collection<Note> allNotes;

    ListView notesListView;

    public static final int NOTE_DETAILS_CODE = 0;
    public static final int NOTE_CHANGED_CODE = 1;

    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.mm.yyyy HH:mm", Locale.getDefault());

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allNotes = DbManager.getAllNotes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fabNotes);
        fab.setOnClickListener(btn -> createNewNote());

        notesListView = view.findViewById(R.id.notesListView);
        setListViewAdapter();
        notesListView.setOnItemClickListener((adapterView, v, pos, id) ->
                editNote(new ArrayList<>(allNotes).get(pos)));
        registerForContextMenu(notesListView);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTE_DETAILS_CODE && resultCode == NOTE_CHANGED_CODE) {
            allNotes = DbManager.getAllNotes();
            setListViewAdapter();
        }
    }

    private void createNewNote() {
        Note note = new Note(-1, "", new Date(System.currentTimeMillis()));
        editNote(note);
    }

    private void editNote(Note note) {
        Intent intent = new Intent(getContext(), NoteDetailsActivity.class);
        intent.putExtra("note", note);
        startActivityForResult(intent, NOTE_DETAILS_CODE);
    }

    private void setListViewAdapter() {
        List<HashMap<String, String>> data = new ArrayList<>(allNotes.size());

        HashMap<String, String> map;
        for (Note n : allNotes) {
            map = new HashMap<>(2);

            map.put("id", String.valueOf(n.getId()));
            map.put("text", n.getText());
            map.put("lastChangeTime", "Последнее изменение: " +
                    dateFormat.format(n.getLastChangeTime()));

            data.add(map);
        }

        String[] from = {"text", "lastChangeTime"};
        int[] to = {R.id.listItemTask_twText, R.id.listItemTask_twLastChangeTime};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.list_item_note, from, to);
        notesListView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.equals(notesListView)) menu.add("Удалить заметку");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Удалить заметку")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Удалить заметку")
                    .setMessage("Вы действительно хотите удалить заметку?")
                    .setNegativeButton("Нет", (di, i) -> di.cancel())
                    .setPositiveButton("Да", (di, i) -> {
                        AdapterView.AdapterContextMenuInfo menuInfo =
                                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                        HashMap<String, String> noteMap = (HashMap<String, String>)
                                notesListView.getItemAtPosition(menuInfo.position);
                        DbManager.deleteNote(Integer.valueOf(noteMap.get("id")));
                        allNotes = DbManager.getAllNotes();
                        setListViewAdapter();
                    })
                    .show();
        }
        return true;
    }

    /*private Note getNoteById(int id) {
        for (Note n: allNotes) if (n.getId() == id) return n;
        throw new RuntimeException("Записи с ID = " + id + " не существует");
    }

    private Note getNoteById(String id) {
        return getNoteById(Integer.parseInt(id));
    }*/
}
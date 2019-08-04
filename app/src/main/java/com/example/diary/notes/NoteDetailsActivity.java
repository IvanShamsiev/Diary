package com.example.diary.notes;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.db.DbManager;

import java.util.Date;

import static com.example.diary.notes.NotesFragment.NOTE_CHANGED_CODE;
import static com.example.diary.notes.NotesFragment.dateFormat;

public class NoteDetailsActivity extends AppCompatActivity {

    TextView twLastChangeTime;
    EditText editNote;

    Note note;

    AlertDialog closeDialog, deleteDialog;

    MenuItem saveBtn;

    boolean isNewNote;
    boolean saved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        setTitle("Заметка");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        int id = getIntent().getIntExtra("noteId", Integer.MIN_VALUE);
        if (id == -1) {
            isNewNote = true;
            note = new Note(-1, "", new Date(System.currentTimeMillis()));
        }
        else note = DbManager.getNoteById(id);

        twLastChangeTime = findViewById(R.id.twLastChangeTime);
        editNote = findViewById(R.id.etNoteText);

        setLastChangeTime(note.getLastChangeTime());
        editNote.setText(note.getText());

        if (note.getText().isEmpty()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            editNote.requestFocus();
        }

        editNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                saved = false;
                if (!saveBtn.isVisible()) saveBtn.setVisible(true);
            }
        });



        closeDialog = new AlertDialog.Builder(this)
                .setTitle("Сохранить?")
                .setMessage("Заметка была изменена, сохранить изменения?")
                .setPositiveButton("Да", (di, i) -> {
                    saveNote();
                    setResult(NOTE_CHANGED_CODE);
                    finish();
                })
                .setNegativeButton("Нет", (di, i) -> finish())
                .setNeutralButton("Отмена", (di, i) -> di.cancel())
                .create();

        deleteDialog = new AlertDialog.Builder(this)
                .setTitle("Удалить заметку")
                .setMessage("Вы действительно хотите удалить заметку?")
                .setNegativeButton("Нет", (di, i) -> di.cancel())
                .setPositiveButton("Да", (di, i) -> {
                    if (!isNewNote) {
                        DbManager.deleteNote(note.getId());
                        setResult(NOTE_CHANGED_CODE);
                    }
                    finish();
                })
                .create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_details, menu);
        MenuItem deleteItem = menu.getItem(0);
        saveBtn = menu.getItem(1);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            deleteDialog.show();
            return true;
        });
        saveBtn.setOnMenuItemClickListener(menuItem -> {
            saveNote();
            return true;
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean changed = !note.getText().equals(editNote.getText().toString());
        if (!changed || saved) finish();
        else closeDialog.show();
    }

    private void saveNote() {
        String newText = editNote.getText().toString();

        if (newText.isEmpty()) {
            if (!isNewNote) DbManager.deleteNote(note.getId());
            return;
        }

        note.setText(newText);

        note = isNewNote ? DbManager.addNote(note) : DbManager.updateNote(note.getId(), newText);

        setLastChangeTime(note.getLastChangeTime());

        saved = true;

        saveBtn.setVisible(false);
    }

    private void setLastChangeTime(Date lastChangeTime) {
        twLastChangeTime.setText("Последнее изменение: " + dateFormat.format(lastChangeTime));
    }
}

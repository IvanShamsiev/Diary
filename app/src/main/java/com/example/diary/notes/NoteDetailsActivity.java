package com.example.diary.notes;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.example.diary.db.DiaryDao;

import java.util.Date;

import static com.example.diary.notes.NotesAdapter.dateFormat;
import static com.example.diary.notes.NotesFragment.NOTE_CHANGED_CODE;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText editNote;

    Note note;

    AlertDialog closeDialog, deleteDialog;

    MenuItem saveBtn;

    boolean isNewNote;
    boolean saved = true;

    boolean autoSaveActive = false;
    boolean saveDialogActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        else note = DiaryDao.getNoteById(id);
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
                if (autoSaveActive) saveNote();
                else if (!saveBtn.isVisible()) saveBtn.setVisible(true);
            }
        });


        autoSaveActive = preferences.getBoolean("notes_auto_save_switch_preference", false);
        saveDialogActive = preferences.getBoolean("notes_save_dialog_switch_preference", true);
        float textSize = 20;
        try {
            String size = preferences.getString("notes_text_size_preference", "20");
            textSize = Float.parseFloat(size);
        } catch (Exception ignored) { }
        editNote.setTextSize(textSize);

        if (saveDialogActive) closeDialog = new AlertDialog.Builder(this)
                .setTitle("Сохранить?")
                .setMessage("Заметка была изменена, сохранить изменения?")
                .setPositiveButton("Да", (di, i) -> {
                    saveNote();
                    finish();
                })
                .setNegativeButton("Нет", (di, i) -> finish())
                .setNeutralButton("Отмена", (di, i) -> di.cancel())
                .create();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_details, menu);
        MenuItem deleteItem = menu.getItem(0);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            if (deleteDialog == null)
                deleteDialog = new AlertDialog.Builder(this)
                        .setTitle("Удалить заметку")
                        .setMessage("Вы действительно хотите удалить заметку?")
                        .setNegativeButton("Нет", (di, i) -> di.cancel())
                        .setPositiveButton("Да", (di, i) -> {
                            if (!isNewNote) {
                                DiaryDao.deleteNote(note.getId());
                                setResult(NOTE_CHANGED_CODE);
                            }
                            finish();
                        })
                        .create();
            deleteDialog.show();
            return true;
        });
        saveBtn = menu.getItem(1);
        saveBtn.setVisible(false);
        saveBtn.setOnMenuItemClickListener(menuItem -> {
            saveNote();
            menuItem.setVisible(false);
            return true;
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean changed = !note.getText().equals(editNote.getText().toString());
        if (!changed || saved) finish();
        else if (saveDialogActive) closeDialog.show();
        else { saveNote(); finish(); }
    }

    private void saveNote() {
        String newText = editNote.getText().toString();

        if (newText.isEmpty()) {
            if (!isNewNote) {
                DiaryDao.deleteNote(note.getId());
                setResult(NOTE_CHANGED_CODE);
            }
            return;
        }

        note.setText(newText);

        note = isNewNote ? DiaryDao.addNote(note) : DiaryDao.updateNote(note.getId(), newText);
        isNewNote = false;

        setLastChangeTime(note.getLastChangeTime());

        saved = true;

        setResult(NOTE_CHANGED_CODE);
    }

    private void setLastChangeTime(Date lastChangeTime) {
        TextView twLastChangeTime = findViewById(R.id.twLastChangeTime);
        twLastChangeTime.setText("Последнее изменение: " + dateFormat.format(lastChangeTime));
    }
}

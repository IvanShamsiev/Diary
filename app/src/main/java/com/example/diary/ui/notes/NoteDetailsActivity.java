package com.example.diary.ui.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.diary.model.Note;

import java.util.Date;

import static com.example.diary.DiaryApp.dateFormat;
import static com.example.diary.ui.prefs.SettingsFragment.NOTES_AUTO_SAVE_PREF;
import static com.example.diary.ui.prefs.SettingsFragment.NOTES_SAVE_DIALOG_PREF;
import static com.example.diary.ui.prefs.SettingsFragment.NOTES_TEXT_SIZE_PREF;

public class NoteDetailsActivity extends AppCompatActivity {

    // Model
    Note note;

    // UI
    TextView twLastChangeTime;
    EditText editNote;
    AlertDialog closeDialog, deleteDialog;
    MenuItem saveBtn;

    // Preferences
    boolean autoSaveActive = false;
    boolean saveDialogActive = true;

    // Save indicator
    boolean saved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        setTitle(R.string.note);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // Set note from DB or new
        long id = getIntent().getLongExtra("noteId", -1);
        if (id == -1) note = Note.getEmptyNote();
        else note = DiaryDao.getNoteById(id);

        // Set UI
        twLastChangeTime = findViewById(R.id.twLastChangeTime);
        setLastChangeTime(note.getLastChangeTime());
        editNote = findViewById(R.id.etNoteText);
        editNote.setText(note.getText());
        if (note.getText().isEmpty()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            editNote.requestFocus();
        }

        // Set watching for change text
        editNote.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                saved = false;
                if (autoSaveActive) saveNote();
                else if (!saveBtn.isVisible()) saveBtn.setVisible(true);
            }
        });


        // Set values from settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoSaveActive = preferences.getBoolean(NOTES_AUTO_SAVE_PREF, false);
        saveDialogActive = preferences.getBoolean(NOTES_SAVE_DIALOG_PREF, true);
        float textSize = 20;
        try {
            String size = preferences.getString(NOTES_TEXT_SIZE_PREF, "20");
            textSize = Float.parseFloat(size);
        } catch (Exception ignored) { }
        editNote.setTextSize(textSize);


        // Set save dialog before closing
        if (saveDialogActive) closeDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.save_note_question)
                .setMessage(R.string.save_note_msg)
                .setPositiveButton(R.string.yes, (di, i) -> {
                    saveNote();
                    finish();
                })
                .setNegativeButton(R.string.no, (di, i) -> finish())
                .setNeutralButton(R.string.cancel, (di, i) -> di.cancel())
                .create();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_details, menu);

        // Set delete note item
        MenuItem deleteItem = menu.getItem(0);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            if (deleteDialog == null)
                deleteDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_note_question)
                        .setMessage(R.string.delete_note_msg)
                        .setNegativeButton(R.string.no, (di, i) -> di.cancel())
                        .setPositiveButton(R.string.yes, (di, i) -> {
                            DiaryDao.removeNote(note.getId());
                            setResult(Activity.RESULT_OK);
                            finish();
                        })
                        .create();
            deleteDialog.show();
            return true;
        });

        // Set save note item
        saveBtn = menu.getItem(1);
        saveBtn.setVisible(false);
        saveBtn.setOnMenuItemClickListener(menuItem -> {
            saveNote();
            saveBtn.setVisible(false);
            return true;
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        boolean changed = !note.getText().equals(editNote.getText().toString());
        if (!changed || saved) finish();
        else if (!saveDialogActive) { saveNote(); finish(); }
        else closeDialog.show();
    }

    private void saveNote() {
        note.setText(editNote.getText().toString());
        note = DiaryDao.saveNote(note);
        setLastChangeTime(note.getLastChangeTime());
        saved = true;
        setResult(Activity.RESULT_OK);
    }

    private void setLastChangeTime(Date lastChangeTime) {
        twLastChangeTime.setText(String.format(getString(R.string.last_change), dateFormat.format(lastChangeTime)));
    }

    // Return intent for start this activity
    static Intent getIntent(Context context, long noteId) {
        Intent intent = new Intent(context, NoteDetailsActivity.class);
        intent.putExtra(NotesAdapter.INTENT_NOTE_ID, noteId);
        return intent;
    }
}

package com.example.diary.ui.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.example.diary.R;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String NOTES_AUTO_SAVE_PREF = "notes_auto_save_switch_preference";
    public static final String NOTES_SAVE_DIALOG_PREF = "notes_save_dialog_switch_preference";
    public static final String NOTES_TEXT_SIZE_PREF = "notes_text_size_preference";

    SwitchPreference notesAutoSavePref;
    SwitchPreference notesSaveDialogPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        notesAutoSavePref = (SwitchPreference) findPreference(NOTES_AUTO_SAVE_PREF);
        notesSaveDialogPref = (SwitchPreference) findPreference(NOTES_SAVE_DIALOG_PREF);

        notesCheckAutoSave(notesAutoSavePref, notesSaveDialogPref);
    }

    @Override
    public void onResume() {
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(NOTES_AUTO_SAVE_PREF)) {
            notesCheckAutoSave(notesAutoSavePref, notesSaveDialogPref);
        }
    }

    private void notesCheckAutoSave(SwitchPreference autoSavePref, SwitchPreference saveDialogPref) {
        if (autoSavePref.isChecked()) {
            saveDialogPref.setChecked(false);
            saveDialogPref.setEnabled(false);
        } else saveDialogPref.setEnabled(true);
    }
}

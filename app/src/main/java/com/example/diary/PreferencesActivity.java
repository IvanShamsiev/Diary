package com.example.diary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class PreferencesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefs);

            checkAutoSave((SwitchPreference) findPreference("notes_auto_save_switch_preference"),
                    (SwitchPreference) findPreference("notes_save_dialog_switch_preference"));

        }

        @Override
        public void onResume() {
            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("notes_auto_save_switch_preference")) {
                checkAutoSave((SwitchPreference) findPreference("notes_auto_save_switch_preference"),
                        (SwitchPreference) findPreference("notes_save_dialog_switch_preference"));
            }
        }

        private void checkAutoSave(SwitchPreference autoSavePref, SwitchPreference saveDialogPref) {
            if (autoSavePref.isChecked()) {
                saveDialogPref.setChecked(false);
                saveDialogPref.setEnabled(false);
            } else saveDialogPref.setEnabled(true);
        }
    }
}
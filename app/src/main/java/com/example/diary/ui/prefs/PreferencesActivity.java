package com.example.diary.ui.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PreferencesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, PreferencesActivity.class);
    }
}
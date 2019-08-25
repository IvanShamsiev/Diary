package com.example.diary;

import android.app.Application;

import com.example.diary.db.DbHelper;
import com.example.diary.db.DiaryDao;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DiaryApp extends Application {

    public static final String LOG_TAG = "MyLogTag";

    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.mm.yyyy HH:mm", Locale.getDefault());

    @Override
    public void onCreate() {
        super.onCreate();
        DiaryDao.setDb(new DbHelper(this).getWritableDatabase());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DiaryDao.destroy();
    }
}

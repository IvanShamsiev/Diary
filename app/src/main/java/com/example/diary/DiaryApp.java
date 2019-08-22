package com.example.diary;

import android.app.Application;

import com.example.diary.db.DbHelper;
import com.example.diary.db.DiaryDao;

public class DiaryApp extends Application {

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

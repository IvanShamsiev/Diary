package com.example.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import static com.example.diary.DiaryApp.LOG_TAG;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, "MyDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, " --- Создание базы данных --- ");

        //db.execSQL("PRAGMA foreign_keys = ON");

        db.execSQL("create table Notes (" +
                "id integer primary key, " +
                "text text, " +
                "lastChangeTime text" +
                ")"
        );

        db.execSQL("create table Tasks (" +
                "id integer primary key, " +
                "name text, " +
                "description text, " +
                "progress integer," +
                "lastChangeTime integer," +
                "childFor integer" +
                ")"
        );


        String currentDate = String.valueOf(System.currentTimeMillis());

        List<String> defaultNotesText = Arrays.asList(
                "Текст записи 1", "Текст записи 2", "Текст записи 3", "Текст записи 4", "Текст записи 5");
        List<String> defaultTasksName = Arrays.asList(
                "Задача 1", "Задача 2", "Задача 3", "Задача 4", "Задача 5", "Задача 6", "Задача 7", "Задача 8", "Задача 9", "Задача 10");
        List<String> defaultTasksDescription = Arrays.asList(
                "Описание задачи 1", "Описание задачи 2", "Описание задачи 3", "Описание задачи 4", "Описание задачи 5", "Описание задачи 6", "Описание задачи 7", "Описание задачи 8", "Описание задачи 9", "Описание задачи 10");
        List<Integer> defaultTasksChildFor = Arrays.asList(-1, -1, -1, 1, 2, 2, 5, 5, 5, 5);



        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < defaultNotesText.size(); i++) {
            contentValues.clear();
            contentValues.put("text", defaultNotesText.get(i));
            contentValues.put("lastChangeTime", currentDate);
            db.insert("Notes", null, contentValues);
        }

        for (int i = 0; i < defaultTasksName.size(); i++) {
            contentValues.clear();
            contentValues.put("name", defaultTasksName.get(i));
            contentValues.put("description", defaultTasksDescription.get(i));
            contentValues.put("progress", 0);
            contentValues.put("lastChangeTime", System.currentTimeMillis());
            contentValues.put("childFor", defaultTasksChildFor.get(i));
            db.insert("Tasks", null, contentValues);
        }

        Log.d(LOG_TAG, " --- База данных успешно создана --- ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
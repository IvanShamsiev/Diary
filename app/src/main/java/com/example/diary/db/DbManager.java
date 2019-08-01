package com.example.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.diary.notes.Note;
import com.example.diary.tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static com.example.diary.MainActivity.LOG_TAG;

public class DbManager {

    private static SQLiteDatabase db;

    public static void setDbContext(Context context) {
        DbManager.db = new DbHelper(context).getWritableDatabase();
    }

    public static Collection<Note> getAllNotes() {
        Cursor c = db.rawQuery("SELECT * FROM Notes", null);

        Collection<Note> notes = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                notes.add(new Note(c.getString(0), c.getString(1), c.getString(2)));
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "Таблица пуста");

        c.close();

        return notes;
    }

    public static Note addNote(String text) {
        ContentValues cv = new ContentValues(2);

        long currentTime = System.currentTimeMillis();

        cv.put("text", text);
        cv.put("lastChangeTime", currentTime);

        int id = (int) db.insert("Notes", null, cv);

        return new Note(id, text, new Date(currentTime));
    }

    public static Note updateNote(int id, String text) {
        ContentValues cv = new ContentValues(2);

        long currentTime = System.currentTimeMillis();

        cv.put("text", text);
        cv.put("lastChangeTime", currentTime);

        db.update("Notes", cv, "id = ?", getId(id));

        return new Note(id, text, new Date(currentTime));
    }

    public static void deleteNote(int id) {
        db.delete("Notes","id = ?", getId(id));
    }


    public static Collection<Task> getAllTasks() {
        Cursor c = db.rawQuery("SELECT * FROM Tasks", null);

        Collection<Task> tasks = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                tasks.add(new Task(c.getString(0), c.getString(1), c.getString(2), c.getString(3)));
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "Таблица пуста");

        c.close();

        return tasks;
    }


    private static String[] getId(int id) {
        return new String[] { String.valueOf(id) };
    }

    public static void destroy() {
        db.close();
    }
}

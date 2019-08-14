package com.example.diary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.diary.notes.Note;
import com.example.diary.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.diary.MainActivity.LOG_TAG;

public class DiaryDao {

    private static SQLiteDatabase db;

    private static List<Note> allNotes;
    private static List<Task> allTasks;

    public static void setDb(SQLiteDatabase db) {
        DiaryDao.db = db;

        updateNotes();
        updateTasks();
    }




    public static void updateNotes() {
        Cursor c = db.rawQuery("SELECT * FROM Notes", null);

        List<Note> notes = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                notes.add(new Note(c.getString(0), c.getString(1), c.getString(2)));
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "Таблица пуста");

        c.close();

        allNotes = notes;
    }

    public static List<Note> getAllNotes() {
        return allNotes;
    }

    public static Note getNoteById(int id) {
        for (Note n: allNotes) if (n.getId() == id) return n;
        throw new RuntimeException("Записи с ID = " + id + " не существует");
    }

    public static Note getNoteById(String id) {
        return getNoteById(Integer.parseInt(id));
    }

    public static Note addNote(Note n) {
        ContentValues cv = new ContentValues(2);

        long currentTime = System.currentTimeMillis();

        cv.put("text", n.getText());
        cv.put("lastChangeTime", currentTime);

        int id = (int) db.insert("Notes", null, cv);

        return new Note(id, n.getText(), new Date(currentTime));
    }

    public static Note updateNote(int id, String text) {
        ContentValues cv = new ContentValues(2);

        long currentTime = System.currentTimeMillis();

        cv.put("text", text);
        cv.put("lastChangeTime", currentTime);

        db.update("Notes", cv, "id = ?", getStringArrayId(id));

        return new Note(id, text, new Date(currentTime));
    }

    public static void deleteNote(int id) {
        db.delete("Notes","id = ?", getStringArrayId(id));
    }





    public static void updateTasks() {
        Cursor c = db.rawQuery("SELECT * FROM Tasks", null);

        List<Task> tasks = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                tasks.add(new Task(c.getString(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("childFor"))));
            } while (c.moveToNext());
        } else Log.d(LOG_TAG, "Таблица пуста");

        c.close();

        allTasks = tasks;
        setTasksChildren();
    }

    private static void setTasksChildren() {
        for (Task child: allTasks)
            if (child.getChildFor() != Task.NOT_CHILD)
                getTaskById(child.getChildFor()).addChildTask(child);
    }

    public static List<Task> getAllTasks() {
        return allTasks;
    }

    public static Task getTaskById(int id) {
        for (Task t: allTasks) if (t.getId() == id) return t;
        throw new RuntimeException("Задачи с id = " + id + " не существует");
    }

    public static Task getTaskById(String id) {
        return getTaskById(Integer.parseInt(id));
    }

    public static Task addTask(Task t) {
        ContentValues cv = new ContentValues(4);

        cv.put("name", t.getName());
        cv.put("description", t.getDescription());
        cv.put("childFor", t.getChildFor());

        int id = (int) db.insert("Tasks", null, cv);

        return new Task(id, t.getName(), t.getDescription(), t.getChildFor());
    }

    public static Task updateTask(Task t) {
        ContentValues cv = new ContentValues(4);

        cv.put("name", t.getName());
        cv.put("description", t.getDescription());
        cv.put("childFor", t.getChildFor());

        db.update("Tasks", cv, "id = ?", getStringArrayId(t.getId()));

        return t;
    }

    public static void deleteTask(int id) {
        db.delete("Tasks","id = ?", getStringArrayId(id));
    }





    private static String[] getStringArrayId(int id) {
        return new String[] { String.valueOf(id) };
    }

    public static void destroy() {
        db.close();
    }

}

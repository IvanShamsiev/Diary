package com.example.diary.notes;

import java.util.Date;

public class Note {

    private int id;
    private String text;
    private Date lastChangeTime;

    public Note(int id, String text, Date lastChangeTime) {
        this.id = id;
        this.text = text;
        this.lastChangeTime = lastChangeTime;
    }

    public Note(String id, String text, String lastChangeTime) {
        this.id = Integer.parseInt(id);
        this.text = text;
        this.lastChangeTime = new Date(Long.parseLong(lastChangeTime));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public void setLastChangeTime(long lastChangeTime) {
        this.lastChangeTime = new Date(lastChangeTime);
    }
}

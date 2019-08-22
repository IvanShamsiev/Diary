package com.example.diary.model;

import java.util.Date;

public class Note {

    private long id;
    private String text;
    private Date lastChangeTime;

    public Note(long id, String text, long lastChangeTime) {
        this.id = id;
        this.text = text;
        this.lastChangeTime = new Date(lastChangeTime);
    }

    public Note(long id, String text, Date lastChangeTime) {
        this.id = id;
        this.text = text;
        this.lastChangeTime = lastChangeTime;
    }

    public Note(String id, String text, String lastChangeTime) {
        this.id = Long.parseLong(id);
        this.text = text;
        this.lastChangeTime = new Date(Long.parseLong(lastChangeTime));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public static Note getEmptyNote() {
        return new Note(-1, "", System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", lastChangeTime=" + lastChangeTime +
                '}';
    }
}

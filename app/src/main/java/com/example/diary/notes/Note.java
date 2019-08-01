package com.example.diary.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeValue(lastChangeTime);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            int id = source.readInt();
            String text = source.readString();
            Date lastChangeTime = (Date) source.readValue(ClassLoader.getSystemClassLoader());
            return new Note(id, text, lastChangeTime);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}

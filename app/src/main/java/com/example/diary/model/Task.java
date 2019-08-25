package com.example.diary.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Task {

    public static final int NOT_CHILD = -1;
    public static final int NONE = -1;

    private long id;
    private String name;
    private String description;
    private int progress;
    private Date lastChangeTime;

    private long childFor;

    private Collection<Task> childTasks;

    public Task(long id, String name, String description, int progress, Date lastChangeTime, long childFor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = progress;
        this.lastChangeTime = lastChangeTime;
        this.childFor = childFor;
        this.childTasks = new ArrayList<>();
    }

    public Task(long id, String name, String description, int progress, long lastChangeTime, long childFor) {
        this(id, name, description, progress, new Date(lastChangeTime), childFor);
    }

    public Task(String id, String name, String description, String progress, String lastChangeTime, String childFor) {
        this(Long.parseLong(id),
                name,
                description,
                Integer.parseInt(progress),
                Long.parseLong(lastChangeTime),
                Long.parseLong(childFor));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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

    public long getChildFor() {
        return childFor;
    }

    public void setChildFor(long childFor) {
        this.childFor = childFor;
    }

    public Collection<Task> getChildTasks() {
        return childTasks;
    }

    public void setChildTasks(Collection<Task> childTasks) {
        this.childTasks = childTasks;
    }

    public void addChildTask(Task childTask) {
        childTasks.add(childTask);
    }

    @Override
    @NonNull
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", progress=" + progress +
                ", lastChangeTime=" + lastChangeTime +
                ", childFor=" + childFor +
                ", childTasks=" + childTasks +
                '}';
    }

    public static Task getEmptyTask() {
        return new Task(NONE, "", "", 0, System.currentTimeMillis(), NOT_CHILD);
    }
}

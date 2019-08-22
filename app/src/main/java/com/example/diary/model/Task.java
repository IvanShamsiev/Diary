package com.example.diary.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class Task {

    public static final int NOT_CHILD = -1;

    private int id;
    private String name;
    private String description;

    private int childFor;

    private Collection<Task> childTasks;

    public Task(int id, String name, String description, int childFor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.childFor = childFor;
        this.childTasks = new ArrayList<>();
    }

    public Task(String id, String name, String description, String childFor) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.childFor = Integer.parseInt(childFor);
        this.childTasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getChildFor() {
        return childFor;
    }

    public void setChildFor(int childFor) {
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

    @NonNull
    @Override
    public String toString() {
        return "Name = " + name + ", id = " + id;
    }
}

package com.example.diary.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class Task {

    public static final int NOT_CHILD = -1;

    private int id;
    private String name;
    private String category;

    private int childFor;

    private Collection<Task> childTasks;

    public Task(int id, String name, String category, int childFor) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.childFor = childFor;
        this.childTasks = new ArrayList<>();
    }

    public Task(String id, String name, String category, String childFor) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.category = category;
        this.childFor = Integer.parseInt(childFor);
        this.childTasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getChildFor() {
        return childFor;
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

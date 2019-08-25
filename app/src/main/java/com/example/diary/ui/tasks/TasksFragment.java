package com.example.diary.ui.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Task;

import java.util.ArrayList;

public class TasksFragment extends Fragment {

    TasksAdapter adapter;

    public static final int TASK_DETAILS_CODE = 0;

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);


        // Set fab
        FloatingActionButton fab = view.findViewById(R.id.fabTasks);
        fab.setOnClickListener(btn -> createNewTask());

        // Set adapter
        adapter = new TasksAdapter(Task.NOT_CHILD, this::editTask);
        adapter.setTasks(new ArrayList<>(DiaryDao.getAllTasks()));

        // Set recycler view
        RecyclerView tasksRecyclerView = view.findViewById(R.id.recyclerViewTasks);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        tasksRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == TASK_DETAILS_CODE) adapter.dataUpdate();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = TasksAdapter.getTaskIdFromIntent(item.getIntent());
        if (item.getTitle().equals(getString(R.string.delete_task_menu_item))) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_task_question)
                    .setMessage(R.string.delete_task_msg)
                    .setNegativeButton(R.string.no, (di, i) -> di.cancel())
                    .setPositiveButton(R.string.yes, (di, i) -> {
                        DiaryDao.deleteTask(id);
                        adapter.dataUpdate();
                    })
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNewTask() {
        Task task = Task.getEmptyTask();
        editTask(task);
    }

    private void editTask(Task task) {
        Intent intent = TaskDetailsActivity.getIntent(getContext(), task.getId());
        startActivityForResult(intent, TASK_DETAILS_CODE);
    }

}
package com.example.diary.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder>  {

    private long showChildFor;
    private List<Task> tasks;
    private OnTaskClickListener onTaskClickListener;
    private static final String INTENT_TASK_ID = "taskId";


    TasksAdapter(long showChildFor, OnTaskClickListener listener) {
        this.showChildFor = showChildFor;
        onTaskClickListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task,
                parent, false);

        return new TaskViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int i) {
        taskViewHolder.bind(tasks.get(i));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    void setTasks(List<Task> tasks) {
        List<Task> showTasks = new ArrayList<>();
        for (Task t: tasks) if (t.getChildFor() == showChildFor) showTasks.add(t);
        Collections.sort(showTasks, (t1, t2) ->
                t2.getLastChangeTime().compareTo(t1.getLastChangeTime()));
        this.tasks = showTasks;
        notifyDataSetChanged();
    }

    void dataUpdate() {
        DiaryDao.updateTasks();
        setTasks(DiaryDao.getAllTasks());
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView twName;
        ImageButton btnChildTasks;
        ProgressBar taskProgressBar;
        RecyclerView childTasksRecyclerView;

        long taskId;

        TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            twName = itemView.findViewById(R.id.twName);
            btnChildTasks = itemView.findViewById(R.id.btnChildTasks);
            taskProgressBar = itemView.findViewById(R.id.taskProgressBar);
            childTasksRecyclerView = itemView.findViewById(R.id.childTasksRecyclerView);

            childTasksRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false));


            itemView.findViewById(R.id.mainLayout).setOnClickListener(
                    v -> onTaskClickListener.onClick(tasks.get(getLayoutPosition())));
            itemView.findViewById(R.id.mainLayout).setOnCreateContextMenuListener(this);
        }

        void bind(Task task) {
            taskId = task.getId();
            twName.setText(task.getName());
            taskProgressBar.setProgress(task.getProgress());

            if (!task.getChildTasks().isEmpty()) {
                btnChildTasks.setVisibility(View.VISIBLE);
                btnChildTasks.setOnClickListener(btn -> {
                    if (childTasksRecyclerView.getVisibility() == View.VISIBLE) {
                        childTasksRecyclerView.setVisibility(View.GONE);
                        btnChildTasks.setImageResource(android.R.drawable.arrow_down_float);
                    } else {
                        childTasksRecyclerView.setVisibility(View.VISIBLE);
                        btnChildTasks.setImageResource(android.R.drawable.arrow_up_float);
                    }
                });
            } else btnChildTasks.setVisibility(View.GONE);

            TasksAdapter adapter = new TasksAdapter(task.getId(), onTaskClickListener);
            adapter.setTasks(new ArrayList<>(DiaryDao.getAllTasks()));
            childTasksRecyclerView.setAdapter(adapter);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo cmi) {
            MenuItem item = menu.add(v.getContext().getString(R.string.delete_task_menu_item));
            item.setIntent(new Intent().putExtra(INTENT_TASK_ID, taskId));
        }
    }

    static long getTaskIdFromIntent(Intent intent) {
        return intent.getLongExtra(INTENT_TASK_ID, Task.NONE);
    }

    interface OnTaskClickListener {
        void onClick(Task task);
    }
}

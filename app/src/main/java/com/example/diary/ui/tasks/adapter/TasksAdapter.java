package com.example.diary.ui.tasks.adapter;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.util.Collections;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder>  {

    private List<Task> tasks;
    private OnTaskClickListener onTaskClickListener;

    private TaskViewHolder parentViewHolder;


    private TasksAdapter(List<Task> tasks, OnTaskClickListener listener, TaskViewHolder parentViewHolder) {
        this.tasks = tasks;
        this.onTaskClickListener = listener;
        this.parentViewHolder = parentViewHolder;
    }

    public TasksAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this(tasks, listener, null);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task,
                parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int i) {
        taskViewHolder.bind(tasks.get(i));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void dataUpdate() {
        DiaryDao.updateTasks();

        long parentTaskId = parentViewHolder == null ? Task.NOT_CHILD : parentViewHolder.getTaskId();
        List<Task> showTasks = DiaryDao.getChildrenForTask(parentTaskId);
        Collections.sort(showTasks, (t1, t2) ->
                t2.getLastChangeTime().compareTo(t1.getLastChangeTime()));
        this.tasks = showTasks;

        notifyDataSetChanged();
    }

    private void removeTask(Task task) {
        int position = tasks.indexOf(task);

        tasks.remove(task);
        notifyItemRemoved(position);

        DiaryDao.removeTask(task.getId());
        DiaryDao.updateTasks();

        if (parentViewHolder != null && tasks.isEmpty()) {
            parentViewHolder.onClearRecyclerView();
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        private TextView twName;
        private ImageButton btnChildTasks;
        private ProgressBar taskProgressBar;
        private RecyclerView childTasksRecyclerView;

        private MenuItem deleteItem;
        private AlertDialog deleteDialog;

        private Task task;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            twName = itemView.findViewById(R.id.twName);
            btnChildTasks = itemView.findViewById(R.id.btnChildTasks);
            taskProgressBar = itemView.findViewById(R.id.taskProgressBar);
            childTasksRecyclerView = itemView.findViewById(R.id.childTasksRecyclerView);
            childTasksRecyclerView.setLayoutManager(new LinearLayoutManager(null,
                    LinearLayoutManager.VERTICAL, false));

            itemView.findViewById(R.id.mainLayout).setOnClickListener(this);
            itemView.findViewById(R.id.mainLayout).setOnCreateContextMenuListener(this);

            deleteDialog = new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.delete_task_question)
                    .setMessage(R.string.delete_task_msg)
                    .setNegativeButton(R.string.no, (di, i) -> di.cancel())
                    .setPositiveButton(R.string.yes, (di, i) -> removeTask(task))
                    .create();
        }

        void bind(Task task) {
            this.task = task;
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

            childTasksRecyclerView.setAdapter(
                    new TasksAdapter(task.getChildTasks(), onTaskClickListener, this));
        }

        @Override
        public void onClick(View v) {
            if (onTaskClickListener != null) onTaskClickListener.onClick(task);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo cmi) {
            deleteItem = menu.add(v.getContext().getString(R.string.delete_task_menu_item));
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.equals(deleteItem)) {
                deleteDialog.show();
                return true;
            }
            return false;
        }

        long getTaskId() {
            return task.getId();
        }

        void onClearRecyclerView() {
            btnChildTasks.setVisibility(View.GONE);
        }
    }
}

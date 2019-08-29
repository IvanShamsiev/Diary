package com.example.diary.ui.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;
import com.example.diary.model.Task;
import com.example.diary.ui.tasks.adapter.TasksAdapter;

import static com.example.diary.ui.tasks.TasksFragment.TASK_DETAILS_CODE;

public class TaskDetailsActivity extends AppCompatActivity {

    // Constants for Intent
    private static final String INTENT_TASK_ID = "taskId";
    private static final String INTENT_CHILD_FOR = "childFor";

    // Model
    Task task;

    // UI
    EditText etTaskName;
    EditText etTaskDescription;
    SeekBar seekBarProgress;
    TextView twRecyclerEmpty;
    RecyclerView rvChildTasks;
    Button btnAddChildTask;
    AlertDialog closeDialog, deleteDialog;
    MenuItem saveTaskItem;

    // Child tasks adapter
    TasksAdapter adapter;

    // Save indicator
    boolean saved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        setTitle(R.string.task);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        // Set task from DB or new
        long id = getIntent().getLongExtra(INTENT_TASK_ID, -1);
        if (id == -1) {
            task = Task.getEmptyTask();
            task.setChildFor(getIntent().getLongExtra(INTENT_CHILD_FOR, -1));
        } else task = DiaryDao.getTaskById(id);



        // Set recycler view adapter
        adapter = new TasksAdapter(task.getChildTasks(), this::editChildTask);
        adapter.setOnDeleteTaskListener(() -> setResult(Activity.RESULT_OK));
        adapter.setParentTaskId(task.getId());


        // Bind UI
        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        seekBarProgress = findViewById(R.id.seekBarProgress);
        twRecyclerEmpty = findViewById(R.id.twRecyclerEmpty);
        rvChildTasks = findViewById(R.id.rvChildTasks);
        btnAddChildTask = findViewById(R.id.btnAddChildTask);

        // Set UI
        etTaskName.setText(task.getName());
        etTaskDescription.setText(task.getDescription());
        seekBarProgress.setProgress(task.getProgress());

        // Set UI if not new task
        if (task.getId() != Task.NONE) {
            if (!task.getChildTasks().isEmpty()) {
                twRecyclerEmpty.setVisibility(View.GONE);
                rvChildTasks.setVisibility(View.VISIBLE);
                rvChildTasks.setLayoutManager(new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
                rvChildTasks.setAdapter(adapter);
            } else twRecyclerEmpty.setVisibility(View.VISIBLE);
            btnAddChildTask.setOnClickListener(btn -> createChildTask());
        } else btnAddChildTask.setVisibility(View.GONE);


        // Set dialogs
        closeDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.save_task_question)
                .setMessage(R.string.save_task_msg)
                .setPositiveButton(R.string.yes, (di, i) -> {
                    saveTask();
                    finish();
                })
                .setNegativeButton(R.string.no, (di, i) -> finish())
                .setNeutralButton(R.string.cancel, (di, i) -> di.cancel())
                .create();
        deleteDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.delete_task_question)
                .setMessage(R.string.delete_task_msg)
                .setPositiveButton(R.string.yes, (di, i) -> {
                    DiaryDao.removeTask(task.getId());
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .setNegativeButton(R.string.no, (di, i) -> di.cancel())
                .setNeutralButton(R.string.cancel, (di, i) -> di.cancel())
                .create();


        // Set on change listeners
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence chs, int s, int c, int a) { }
            @Override public void onTextChanged(CharSequence chs, int s, int b, int c) { }
            @Override public void afterTextChanged(Editable e) {
                saved = false;
                saveTaskItem.setVisible(true);
            }
        };
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int p, boolean fu) {
                saved = false;
                saveTaskItem.setVisible(true);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        etTaskName.addTextChangedListener(textWatcher);
        etTaskDescription.addTextChangedListener(textWatcher);
        seekBarProgress.setOnSeekBarChangeListener(seekBarChangeListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == TASK_DETAILS_CODE) {
            adapter.dataUpdate();
            setResult(Activity.RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_delete, menu);

        // Set delete note item
        MenuItem deleteItem = menu.getItem(0);
        deleteItem.setOnMenuItemClickListener(menuItem -> {
            deleteDialog.show();
            return true;
        });

        // Set save note item
        saveTaskItem = menu.getItem(1);
        saveTaskItem.setVisible(false);
        saveTaskItem.setOnMenuItemClickListener(menuItem -> {
            saveTask();
            saveTaskItem.setVisible(false);
            return true;
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        boolean changed = !(task.getName().equals(etTaskName.getText().toString()) &&
                task.getDescription().equals(etTaskDescription.getText().toString()) &&
                (task.getProgress() == seekBarProgress.getProgress()));
        if (!changed || saved) finish();
        //else if (!saveDialogActive) { saveNote(); finish(); }
        else closeDialog.show();
    }

    private void saveTask() {
        task.setName(etTaskName.getText().toString());
        task.setDescription(etTaskDescription.getText().toString());
        task.setProgress(seekBarProgress.getProgress());
        task = DiaryDao.saveTask(task);
        saved = true;
        setResult(Activity.RESULT_OK);
    }

    private void createChildTask() {
        Intent intent = getIntent(this, Task.NONE);
        intent.putExtra(INTENT_CHILD_FOR, task.getId());
        startActivityForResult(intent, TASK_DETAILS_CODE);
    }

    private void editChildTask(long childTaskId) {
        Intent intent = getIntent(this, childTaskId);
        startActivityForResult(intent, TASK_DETAILS_CODE);
    }

    private void editChildTask(Task task) {
        editChildTask(task.getId());
    }

    public static Intent getIntent(Context context, long taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(INTENT_TASK_ID, taskId);
        return intent;
    }
}

package com.example.diary.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.diary.R;

public class TaskDetailsActivity extends AppCompatActivity {

    public static final int TASK_CHANGED_CODE = 1;
    private static final String INTENT_TASK_ID = "taskId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
    }

    public static Intent getIntent(Context context, long taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(INTENT_TASK_ID, taskId);
        return intent;
    }
}

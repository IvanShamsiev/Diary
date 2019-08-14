package com.example.diary.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.diary.R;
import com.example.diary.db.DiaryDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TasksFragment extends Fragment {

    ListView tasksListView;

    public static final int TASK_DETAILS_CODE = 0;
    public static final int TASK_CHANGED_CODE = 1;

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


        FloatingActionButton fab = view.findViewById(R.id.fabTasks);
        fab.setOnClickListener(btn -> createNewTask());

        tasksListView = view.findViewById(R.id.tasksListView);
        setListViewAdapter();
        tasksListView.setOnItemClickListener((adapterView, v, pos, id) -> {
            editTask(getTaskFromMap(adapterView.getItemAtPosition(pos)));
        });
        /*tasksListView.setOnCreateContextMenuListener((contextMenu, view1, contextMenuInfo) ->
                contextMenu.add("Удалить задачу"));
        tasksListView.oncontext*/
        registerForContextMenu(tasksListView);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TASK_DETAILS_CODE && resultCode == TASK_CHANGED_CODE) {
            DiaryDao.updateTasks();
            setListViewAdapter();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.equals(tasksListView)) menu.add("Удалить задачу");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Удалить задачу")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Удалить задачу")
                    .setMessage("Вы действительно хотите удалить задачу?")
                    .setNegativeButton("Нет", (di, i) -> di.cancel())
                    .setPositiveButton("Да", (di, i) -> {
                        AdapterView.AdapterContextMenuInfo menuInfo =
                                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                        Task t = getTaskFromMap(tasksListView.getItemAtPosition(menuInfo.position));
                        DiaryDao.deleteTask(t.getId());
                        DiaryDao.updateTasks();
                        setListViewAdapter();
                    })
                    .show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void setListViewAdapter() {
        List<HashMap<String, String>> data = new ArrayList<>();

        HashMap<String, String> map;

        Collection<Task> mainTasks = new ArrayList<>();
        for (Task t: DiaryDao.getAllTasks()) if (t.getChildFor() == Task.NOT_CHILD) mainTasks.add(t);
        for (Task t : mainTasks) {
            map = new HashMap<>(5);
            map.put("id", String.valueOf(t.getId()));
            map.put("name", t.getName());
            map.put("description", t.getDescription());
            map.put("childFor", t.getChildFor() == Task.NOT_CHILD ? "" : DiaryDao.getTaskById(t.getChildFor()).getName());
            map.put("childTasks", Arrays.toString(t.getChildTasks().toArray()));

            data.add(map);
        }

        String[] from = {"name"};
        int[] to = {R.id.listItemTask_twName};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.list_item_task, from, to);
        tasksListView.setAdapter(adapter);
    }

    private void createNewTask() {
        Task t = new Task(-1, "", "", Task.NOT_CHILD);
        editTask(t);
    }

    private void editTask(Task task) {
        Intent intent = new Intent(getContext(), TaskDetailsActivity.class);
        intent.putExtra("noteId", task.getId());
        startActivityForResult(intent, TASK_DETAILS_CODE);
    }

    private Task getTaskFromMap(Object o) {
        HashMap<String, String> map = (HashMap<String, String>) o;
        return DiaryDao.getTaskById(map.get("id"));
    }

    /*public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }*/

}
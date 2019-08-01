package com.example.diary.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.diary.R;
import com.example.diary.db.DbManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TasksFragment extends Fragment {

    Collection<Task> allTasks;

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allTasks = DbManager.getAllTasks();
        setTasksChildren();

        allTasks.add(new Task("6", "Оочеееееньььь длиииинннооеее иииииииимяяяяяяяяя таскаааааааааааааааааааааа", "Главная категория", "-1"));
        allTasks.add(new Task("7", "Новый таск", "Главная категория", "6"));

    }

    void setTasksChildren() {
        for (Task child: allTasks)
            if (child.getChildFor() != Task.NOT_CHILD) getTaskById(child.getChildFor()).addChildTask(child);
    }

    Task getTaskById(int id) {
        for (Task t: allTasks) if (t.getId() == id) return t;
        throw new RuntimeException("Задачи с id = " + id + " не существует");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        ListView listView = view.findViewById(R.id.tasksListView);


        List<HashMap<String, String>> data = new ArrayList<>(allTasks.size());

        HashMap<String, String> map;
        for (Task t : allTasks) {
            map = new HashMap<>(5);
            map.put("id", String.valueOf(t.getId()));
            map.put("name", t.getName());
            map.put("category", t.getCategory());
            map.put("childFor", t.getChildFor() == Task.NOT_CHILD ? "" : getTaskById(t.getChildFor()).getName());
            map.put("childTasks", Arrays.toString(t.getChildTasks().toArray()));

            data.add(map);
        }

        String[] from = {"name", "childFor"};
        int[] to = {R.id.listItemTask_twName, R.id.listItemTask_twChildFor};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.list_item_task, from, to);
        listView.setAdapter(adapter);

        return view;
    }

    public static void expand(final View v) {
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
}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
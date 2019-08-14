package com.example.diary;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.example.diary.db.DbHelper;
import com.example.diary.db.DiaryDao;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MyLogTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        PagesAdapter pagesAdapter = new PagesAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagesAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        DiaryDao.setDb(new DbHelper(this).getWritableDatabase());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Настройки").setIntent(new Intent(this, PreferencesActivity.class));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        DiaryDao.destroy();
        super.onDestroy();
    }
}

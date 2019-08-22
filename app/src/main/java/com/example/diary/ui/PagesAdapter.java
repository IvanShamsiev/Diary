package com.example.diary.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.diary.ui.notes.NotesFragment;
import com.example.diary.ui.tasks.TasksFragment;

public class PagesAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    PagesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return NotesFragment.newInstance();
            case 1: return TasksFragment.newInstance();
            default: throw new RuntimeException("Неверный номер страницы");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "Заметки";
            case 1: return "Задачи";
            default: throw new RuntimeException("Неверный номер страницы");
        }
    }
}
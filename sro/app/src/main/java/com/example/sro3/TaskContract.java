package com.example.sro3;

import android.provider.BaseColumns;

public class TaskContract {
    private TaskContract() {}

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String NAME = "name";
        public static final String DEADLINE = "deadline";
        public static final String CATEGORY = "category";
    }
}
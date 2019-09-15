package com.kinderlicht.sql;

import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract() {
    }

    public static class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "kinderlicht";
        public static final String COLUMN_NAME = "name";
    }
}

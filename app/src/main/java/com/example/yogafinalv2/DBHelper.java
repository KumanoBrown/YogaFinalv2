package com.example.yogafinalv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "universal_yoga.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourseTable = "CREATE TABLE courses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT)";

        String createClassTable = "CREATE TABLE classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "course_id INTEGER," +
                "name TEXT," +
                "instructor TEXT," +
                "date TEXT," +
                "time TEXT," +
                "location TEXT," +
                "FOREIGN KEY(course_id) REFERENCES courses(id))";

        db.execSQL(createCourseTable);
        db.execSQL(createClassTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS courses");
        onCreate(db);
    }
}

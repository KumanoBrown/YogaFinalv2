package com.example.yogafinalv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "universal_yoga.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCourseTable = "CREATE TABLE courses (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "capacity INTEGER," +
                "duration INTEGER," +
                "description TEXT," +
                "typeofClass TEXT," +
                "dayOfWeek TEXT," +
                "timeofCourse TEXT," +
                "price REAL)";

        String createClassTable = "CREATE TABLE classes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "courseId INTEGER," +
                "date TEXT," +
                "teacher TEXT," +
                "comment TEXT," +
                "FOREIGN KEY(courseId) REFERENCES courses(Id) ON DELETE CASCADE)";

        db.execSQL(createCourseTable);
        db.execSQL(createClassTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS courses");
        onCreate(db);
    }

    // Insert a new class
    public long insertClass(long courseId, String date, String teacher, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("courseId", courseId);
        values.put("date", date);
        values.put("teacher", teacher);
        values.put("comment", comment);
        long id = db.insert("classes", null, values);
        db.close();
        return id;
    }

    // Retrieve all classes for a specific course
    public List<Class> getClassesForCourse(long courseId) {
        List<Class> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("classes", null, "courseId = ?",
                new String[]{String.valueOf(courseId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Class classItem = new Class();
                classItem.id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                classItem.courseId = cursor.getLong(cursor.getColumnIndexOrThrow("courseId"));
                classItem.date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                classItem.teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
                classItem.comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                classes.add(classItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return classes;
    }

    // Get specific class details
    public Class getClassDetails(long courseId, long classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM classes WHERE courseId = ? AND id = ?",
                new String[]{String.valueOf(courseId), String.valueOf(classId)});

        Class classDetails = null;
        if (cursor.moveToFirst()) {
            classDetails = new Class();
            classDetails.id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            classDetails.courseId = cursor.getLong(cursor.getColumnIndexOrThrow("courseId"));
            classDetails.date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            classDetails.teacher = cursor.getString(cursor.getColumnIndexOrThrow("teacher"));
            classDetails.comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
        }

        cursor.close();
        db.close();
        return classDetails;
    }

    // Update a class
    public void updateClass(long classId, long courseId, String date, String teacher, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("teacher", teacher);
        values.put("comment", comment);

        db.update("classes", values, "id = ?", new String[]{String.valueOf(classId)});
        db.close();
    }

    // Delete a class
    public void deleteClass(long classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("classes", "id = ?", new String[]{String.valueOf(classId)});
        db.close();
    }

    // Insert a new course
    public long insertCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("capacity", course.capacity);
        values.put("duration", course.duration);
        values.put("description", course.description);
        values.put("typeofClass", course.typeofClass);
        values.put("dayOfWeek", course.dayOfWeek);
        values.put("timeofCourse", course.timeofCourse);
        values.put("price", course.price);

        long id = db.insert("courses", null, values);
        db.close();
        return id;
    }

    public Course getCourse(long courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses WHERE Id = ?",
                new String[]{String.valueOf(courseId)});

        Course course = null;
        if (cursor.moveToFirst()) {
            course = new Course();
            course.Id = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            course.capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
            course.duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            course.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            course.typeofClass = cursor.getString(cursor.getColumnIndexOrThrow("typeofClass"));
            course.dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"));
            course.timeofCourse = cursor.getString(cursor.getColumnIndexOrThrow("timeofCourse"));
            course.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
        }

        cursor.close();
        db.close();
        return course;
    }

    public void updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("capacity", course.capacity);
        values.put("duration", course.duration);
        values.put("description", course.description);
        values.put("typeofClass", course.typeofClass);
        values.put("dayOfWeek", course.dayOfWeek);
        values.put("timeofCourse", course.timeofCourse);
        values.put("price", course.price);

        db.update("courses", values, "Id = ?", new String[]{String.valueOf(course.Id)});
        db.close();
    }

    public void deleteCourse(long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("courses", "Id = ?", new String[]{String.valueOf(courseId)});
        db.close();
    }

    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses", null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.Id = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
                course.capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
                course.duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
                course.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                course.typeofClass = cursor.getString(cursor.getColumnIndexOrThrow("typeofClass"));
                course.dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"));
                course.timeofCourse = cursor.getString(cursor.getColumnIndexOrThrow("timeofCourse"));
                course.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                courseList.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return courseList;
    }

    public List<Course> searchCourses(String query) {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = "typeofClass LIKE ? OR dayOfWeek LIKE ? OR timeofCourse LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query("courses", null, selection, selectionArgs,
                null, null, null);

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.Id = cursor.getLong(cursor.getColumnIndexOrThrow("Id"));
            course.typeofClass = cursor.getString(cursor.getColumnIndexOrThrow("typeofClass"));
            course.capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
            course.duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            course.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            course.dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"));
            course.timeofCourse = cursor.getString(cursor.getColumnIndexOrThrow("timeofCourse"));
            course.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            courses.add(course);
        }

        cursor.close();
        db.close();
        return courses;
    }
    public List<Class> searchClasses(String searchTerm, String criteria) {
        List<Class> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection;
        String[] selectionArgs;

        switch (criteria) {
            case "teacher":
                selection = "teacher LIKE ?";
                selectionArgs = new String[]{"%" + searchTerm + "%"};
                break;
            case "date":
                selection = "date LIKE ?";
                selectionArgs = new String[]{"%" + searchTerm + "%"};
                break;
            case "dayOfWeek":
                selection = "dayOfWeek LIKE ?"; // Assuming you have a dayOfWeek column in your classes table
                selectionArgs = new String[]{"%" + searchTerm + "%"};
                break;
            default:
                // Handle invalid criteria
                return classes;
        }

        Cursor cursor = db.query("classes", null, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            Class classObj = new Class();
            classObj.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            classObj.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow("teacher")));
            classObj.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            classes.add(classObj);
        }

        cursor.close();
        db.close();
        return classes;
    }
}

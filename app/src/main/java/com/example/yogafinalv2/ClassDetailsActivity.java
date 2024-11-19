package com.example.yogafinalv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ClassDetailsActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        dbHelper = new DBHelper(this); // Initialize DBHelper

        // Get class name and course ID from intent extras

        long courseId = getIntent().getLongExtra("courseId", 0);
        long classId = getIntent().getLongExtra("classId", 0);
        Class classDetails = dbHelper.getClassDetails(courseId, classId);

        // Set class details in the TextViews
        TextView classNameTextView = findViewById(R.id.classNameTextView);
        TextView classDateTextView = findViewById(R.id.classDateTextView);
        TextView classTeacherTextView = findViewById(R.id.classTeacherTextView);
        TextView classCommentTextView = findViewById(R.id.classCommentTextView);

        if (classDetails != null) {
            classNameTextView.setText(String.valueOf(classDetails.id));
            classDateTextView.setText(classDetails.date);
            classTeacherTextView.setText(classDetails.teacher);
            classCommentTextView.setText(classDetails.comment);
        } else {
            //Handle case where class details are not found
            classNameTextView.setText("Class not found");
            classDateTextView.setText("");
            classTeacherTextView.setText(""); // Changed to classTeacherTextView
            classCommentTextView.setText(""); // Changed to classCommentTextView
        }
    }
}
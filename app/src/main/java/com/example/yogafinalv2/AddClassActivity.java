package com.example.yogafinalv2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private long courseId;
    private TextView dateTextView;
    private Calendar calendar;
    private String courseDayOfWeek; // Expected day of week from Course

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_add_class);

        dbHelper = new DBHelper(this);

        // Get courseId and courseDayOfWeek from intent
        courseId = getIntent().getLongExtra("courseId", 0);
        courseDayOfWeek = getIntent().getStringExtra("courseDayOfWeek");


        Course course = dbHelper.getCourse(courseId);
        if (course != null) {
            courseDayOfWeek = course.getDayOfWeek();
        } else {
            Toast.makeText(this, "Error: Course not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dateTextView = findViewById(R.id.dateTextView);
        Button dateButton = findViewById(R.id.dateButton);
        TextInputEditText teacherEditText = findViewById(R.id.teacherEditText);
        TextInputEditText commentEditText = findViewById(R.id.commentEditText);
        Button addClassButton = findViewById(R.id.addClassButton);

        calendar = Calendar.getInstance();

        // Show DatePickerDialog when clicking the date button
        dateButton.setOnClickListener(v -> showDatePickerDialog());

        // Add class button logic
        addClassButton.setOnClickListener(v -> {
            String date = dateTextView.getText().toString();
            String teacher = teacherEditText.getText().toString();
            String comment = commentEditText.getText().toString();

            if (date.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(AddClassActivity.this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert the class into database
            long id = dbHelper.insertClass(courseId, date, teacher, comment);

            if (id != -1) {
                Toast.makeText(AddClassActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddClassActivity.this, ClassDetailsActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("classId", id); // Pass the generated classId
                startActivity(intent);
                // *** End of code snippet ***

                finish();
            } else {
                Toast.makeText(AddClassActivity.this, "Error adding class", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display DatePickerDialog to select date
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String selectedDate = sdf.format(calendar.getTime());

                    if (isValidDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))) {
                        dateTextView.setText(selectedDate);
                    } else {
                        Toast.makeText(AddClassActivity.this, "Invalid date! Course does not run on this day.", Toast.LENGTH_SHORT).show();
                        showDatePickerDialog();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // Validate selected date against course's expected day of the week
    private boolean isValidDayOfWeek(int selectedDayOfWeek) {
        int expectedDayOfWeek = getDayOfWeekIndex(courseDayOfWeek);
        return selectedDayOfWeek == expectedDayOfWeek;
    }
    // Convert day of the week string to Calendar constant
    private int getDayOfWeekIndex(String dayOfWeek) {
        if (dayOfWeek == null) {
            Log.e("AddClassActivity", "dayOfWeek is null in getDayOfWeekIndex");
            return -1; // Or throw an exception if you prefer
        }

        switch (dayOfWeek) {
            case "Sunday":
                return Calendar.SUNDAY;
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
            default:
                Log.e("AddClassActivity", "Invalid dayOfWeek: " + dayOfWeek);
                return -1; // Invalid day
        }
    }
}
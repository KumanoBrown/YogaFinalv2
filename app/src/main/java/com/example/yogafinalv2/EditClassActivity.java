package com.example.yogafinalv2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditClassActivity extends AppCompatActivity {

    private TextView editDateTextView;
    private EditText editTeacherEditText;
    private EditText editCommentEditText;
    private Button updateClassButton;
    private Button editDateButton;
    private long courseId;
    private long classId;
    private DBHelper dbHelper;
    private String courseDayOfWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        editDateTextView = findViewById(R.id.editDateTextView);
        editTeacherEditText = findViewById(R.id.editTeacherEditText);
        editCommentEditText = findViewById(R.id.editCommentEditText);
        updateClassButton = findViewById(R.id.updateClassButton);
        editDateButton = findViewById(R.id.editDateButton);
        dbHelper = new DBHelper(this);

        // Assume you get the expected course day of the week from the intent or database
        courseId = getIntent().getLongExtra("courseId", 0);
        courseDayOfWeek = getIntent().getStringExtra("courseDayOfWeek");

        if (courseDayOfWeek == null) {
            Toast.makeText(this, "Error: Course day of week not found.", Toast.LENGTH_SHORT).show();
            finish(); // Or handle the error differently
            return;
        }


        editDateButton.setOnClickListener(v -> showDatePicker());

        Intent intent = getIntent();
        classId = intent.getLongExtra("classId", 0);

        updateClassButton.setOnClickListener(v -> {
            String date = editDateTextView.getText().toString();
            String teacher = editTeacherEditText.getText().toString();
            String comment = editCommentEditText.getText().toString();

            if (date.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the class in the database
            dbHelper.updateClass(classId, courseId, date, teacher, comment);
            Toast.makeText(this, "Class updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
        );
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());

            if (isValidDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))) {
                editDateTextView.setText(selectedDate);
            } else {
                Toast.makeText(this, "Invalid date! Course does not run on this day.", Toast.LENGTH_SHORT).show();
                showDatePicker(); // Reopen the date picker
            }
        }, year, month, day);

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
            Log.e("EditClassActivity", "dayOfWeek is null in getDayOfWeekIndex");
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
                Log.e("EditClassActivity", "Invalid dayOfWeek: " + dayOfWeek);
                return -1; // Invalid day
        }
    }
}

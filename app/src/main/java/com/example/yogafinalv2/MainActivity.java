package com.example.yogafinalv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView courseRecyclerView;
    private CourseAdapter courseAdapter;
    private SearchView searchView;
    private FloatingActionButton syncButton;



    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        courseRecyclerView = findViewById(R.id.courseRecyclerView); // Initialize here
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Course> courses = dbHelper.getAllCourses();
        courseAdapter = new CourseAdapter(courses);
        courseRecyclerView.setAdapter(courseAdapter);

        courseAdapter.setOnItemClickListener(course -> {
            Intent intent = new Intent(MainActivity.this, ClassActivity.class);
            intent.putExtra("courseId", course.Id);
            startActivity(intent);
        });

        courseAdapter.setOnItemLongClickListener(course -> showEditDeleteDialog(course));

        FloatingActionButton addCourseButton = findViewById(R.id.addCourseButton);
        addCourseButton.setOnClickListener(v -> showAddCourseDialog());

        syncButton = findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(MainActivity.this)) {
                    syncDataWithFirebase();
                } else {
                    Toast.makeText(MainActivity.this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        return true;
    }


    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_course, null);

        final TextInputEditText typeEditText = view.findViewById(R.id.typeEditText);
        final TextInputEditText capacityEditText = view.findViewById(R.id.capacityEditText);
        final TextInputEditText durationEditText = view.findViewById(R.id.durationEditText);
        final TextInputEditText descriptionEditText = view.findViewById(R.id.descriptionEditText);
        Spinner dayOfWeekSpinner = view.findViewById(R.id.dayOfWeekSpinner);
        final TextInputEditText timeEditText = view.findViewById(R.id.timeEditText);
        final TextInputEditText priceEditText = view.findViewById(R.id.priceEditText);

        // Populate Spinner if necessary
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);
        builder.setView(view)
                .setTitle("Add Course")
                .setPositiveButton("Add", (dialog, which) -> {
                    String selectedDayOfWeek = dayOfWeekSpinner.getSelectedItem() != null
                            ? dayOfWeekSpinner.getSelectedItem().toString()
                            : "DefaultDay";

                    try {
                        Course course = new Course();
                        course.typeofClass = typeEditText.getText().toString();
                        course.capacity = Integer.parseInt(capacityEditText.getText().toString());
                        course.duration = Integer.parseInt(durationEditText.getText().toString());
                        course.description = descriptionEditText.getText().toString();
                        course.dayOfWeek = selectedDayOfWeek;
                        course.timeofCourse = timeEditText.getText().toString();
                        course.price = Double.parseDouble(priceEditText.getText().toString());

                        dbHelper.insertCourse(course);
                        updateCourseList();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showEditDeleteDialog(Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditCourseDialog(course);
                            break;
                        case 1:
                            deleteCourse(course);
                            break;
                    }
                    dialog.dismiss();
                })
                .show();
    }

    private void showEditCourseDialog(Course course) {
        Context context = this; // Use 'this' if inside an Activity
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_course, null);

        Spinner dayOfWeekSpinner = view.findViewById(R.id.editDayOfWeekSpinner);
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, daysOfWeek);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(spinnerAdapter);

        // Pre-select current day in Spinner
        int selectedIndex = getDayOfWeekIndex(course.dayOfWeek);
        if (selectedIndex != -1) {
            dayOfWeekSpinner.setSelection(selectedIndex);
        }

        // Initialize EditText fields
        TextInputEditText typeEditText = view.findViewById(R.id.editTypeEditText);
        TextInputEditText capacityEditText = view.findViewById(R.id.editCapacityEditText);
        TextInputEditText durationEditText = view.findViewById(R.id.editDurationEditText);
        TextInputEditText descriptionEditText = view.findViewById(R.id.editDescriptionEditText);
        TextInputEditText timeEditText = view.findViewById(R.id.editTimeEditText);
        TextInputEditText priceEditText = view.findViewById(R.id.editPriceEditText);

        // Set current course values
        typeEditText.setText(course.typeofClass);
        capacityEditText.setText(String.valueOf(course.capacity));
        durationEditText.setText(String.valueOf(course.duration));
        descriptionEditText.setText(course.description);
        timeEditText.setText(course.timeofCourse);
        priceEditText.setText(String.format(Locale.getDefault(), "%.2f", course.price));

        // Show dialog
        builder.setView(view)
                .setTitle("Edit Course")
                .setPositiveButton("Save", (dialog, which) -> {
                    if (validateInputs(typeEditText, capacityEditText, durationEditText, timeEditText, priceEditText)) {
                        course.typeofClass = typeEditText.getText().toString();
                        course.capacity = Integer.parseInt(capacityEditText.getText().toString());
                        course.duration = Integer.parseInt(durationEditText.getText().toString());
                        course.description = descriptionEditText.getText().toString();
                        course.dayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
                        course.timeofCourse = timeEditText.getText().toString();
                        course.price = Double.parseDouble(priceEditText.getText().toString());

                        dbHelper.updateCourse(course);
                        updateCourseList();
                        Toast.makeText(context, "Course updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Helper method to get index of day
    private int getDayOfWeekIndex(String dayOfWeek) {
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i].equals(dayOfWeek)) {
                return i;
            }
        }
        return -1;
    }

    // Validation method to ensure no fields are empty
    private boolean validateInputs(TextInputEditText... fields) {
        for (TextInputEditText field : fields) {
            if (field.getText() == null || field.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    private void deleteCourse(Course course) {
        dbHelper.deleteCourse(course.Id);
        updateCourseList();
        Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateCourseList() {
        List<Course> courses = dbHelper.getAllCourses();
        courseAdapter.updateCourses(courses);
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        } else {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    private void syncDataWithFirebase() {
        DatabaseReference database = FirebaseDatabase.getInstance("https://yoga-finalv2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        List<Course> courses = dbHelper.getAllCourses();
        int totalCourses = courses.size();
        int[] successfulCourseSyncs = {0};
        int[] successfulClassSyncs = {0};

        for (Course course : courses) {
            database.child("courses").child(String.valueOf(course.Id)).setValue(course)
                    .addOnSuccessListener(aVoid -> {
                        successfulCourseSyncs[0]++;
                        Log.d("Firebase Sync", "Course synced: " + course.typeofClass);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase Sync", "Failed to sync course: " + course.typeofClass, e);
                        Toast.makeText(MainActivity.this, "Failed to sync course: " + course.typeofClass, Toast.LENGTH_SHORT).show();
                    });

            List<Class> classes = dbHelper.getClassesForCourse(course.Id);
            for (Class classItem : classes) {
                database.child("classes").child(String.valueOf(classItem.id)).setValue(classItem)
                        .addOnSuccessListener(aVoid -> {
                            successfulClassSyncs[0]++;
                            Log.d("Firebase Sync", "Class synced: " + classItem.id + " for course " + course.typeofClass);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase Sync", "Failed to sync class: " + classItem.id, e);
                            Toast.makeText(MainActivity.this, "Failed to sync class: " + classItem.id, Toast.LENGTH_SHORT).show();
                        });
            }
        }

        // Final sync completion toast
        Toast.makeText(MainActivity.this,
                "Sync completed: " + successfulCourseSyncs[0] + " courses and " + successfulClassSyncs[0] + " classes synced",
                Toast.LENGTH_LONG).show();
    }
}
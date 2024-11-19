package com.example.yogafinalv2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView classRecyclerView;
    private ClassAdapter classAdapter;
    private long courseId;
    private String courseDayOfWeek;
    private SearchView searchView;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        initializeViews();
        setupToolbar();
        checkCourseValidity();
        setupRecyclerView();
        setupSearchView();
        setupSwipeRefresh();
        setupFab();
    }

    private void initializeViews() {
        dbHelper = new DBHelper(this);
        searchView = findViewById(R.id.searchView);
        classRecyclerView = findViewById(R.id.classRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void checkCourseValidity() {
        courseId = getIntent().getLongExtra("courseId", -1);
        if (courseId == -1) {
            Toast.makeText(this, "Invalid course. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Course course = dbHelper.getCourse(courseId);
        if (course != null) {
            courseDayOfWeek = course.getDayOfWeek();
        } else {
            Toast.makeText(this, "Error: Course not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        classRecyclerView.setHasFixedSize(true);
        updateClassList();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        searchView.setQueryHint("Search by teacher or date");
        searchView.setIconifiedByDefault(false);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        if (textView != null) {
            textView.setTextColor(Color.BLACK);  // Change text color
            textView.setHintTextColor(Color.GRAY);  // Change hint color
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateClassList();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupFab() {
        FloatingActionButton addClassButton = findViewById(R.id.addClassButton);
        addClassButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClassActivity.this, AddClassActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            updateClassList();
            return;
        }

        List<Class> allClasses = dbHelper.getClassesForCourse(courseId);
        List<Class> filteredClasses = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (Class classItem : allClasses) {
            if (classItem.getTeacher().toLowerCase().contains(lowerQuery) ||
                    classItem.getDate().toLowerCase().contains(lowerQuery)) {
                filteredClasses.add(classItem);
            }
        }

        updateRecyclerView(filteredClasses);
        updateEmptyViewVisibility(filteredClasses);
    }

    private void showEditDeleteDialog(Class classItem, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        navigateToEditClass(classItem);
                    } else if (which == 1) {
                        deleteClass(classItem);
                    }
                })
                .show();
    }

    private void navigateToEditClass(Class classItem) {
        Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
        intent.putExtra("classId", classItem.id);
        intent.putExtra("courseId", courseId);
        intent.putExtra("courseDayOfWeek", courseDayOfWeek);
        startActivity(intent);
    }

    private void deleteClass(Class classItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteClass(classItem.id);
                    updateClassList();
                    Toast.makeText(this, "Class deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateClassList() {
        List<Class> classes = dbHelper.getClassesForCourse(courseId);
        updateRecyclerView(classes);
        updateEmptyViewVisibility(classes);
    }

    private void updateRecyclerView(List<Class> classes) {
        if (classAdapter == null) {
            classAdapter = new ClassAdapter(classes, courseId);
            classAdapter.setOnItemLongClickListener(this::showEditDeleteDialog);
            classRecyclerView.setAdapter(classAdapter);
        } else {
            classAdapter.updateClasses(classes);
        }
    }

    private void updateEmptyViewVisibility(List<Class> classes) {
        if (classes.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            classRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            classRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateClassList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void updateEmptyView(List<Class> classes) {
        TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setVisibility(classes.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
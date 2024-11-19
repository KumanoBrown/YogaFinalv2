package com.example.yogafinalv2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Course course);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);

        // Set the course type as the main title
        holder.courseNameTextView.setText(course.typeofClass);
        holder.courseTimeTextView.setText(course.timeofCourse);

        // Set the day of the week as text (non-editable)
        holder.courseDayOfWeekTextView.setText("Day: " + course.dayOfWeek);

        // Set the course details
        holder.courseDetailsTextView.setText(String.format(
                "• Duration: %d mins \n• Capacity: %d \n• Price: $%.2f",
                course.duration,
                course.capacity,
                course.price
        ));

        // Set up click listeners
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(course);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(course);
            }
            return true;
        });

        // Open the edit dialog when the day of the week is clicked
        holder.courseDayOfWeekTextView.setOnClickListener(v -> {
            // Pass the itemView to access the context
            openEditDayDialog(course, holder.itemView);
        });
    }


    // Method to open the dialog for editing the day of the week
    private void openEditDayDialog(Course course, View itemView) {
        // Get the context from itemView
        Context context = itemView.getContext();

        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        new AlertDialog.Builder(context)
                .setTitle("Select Day")
                .setItems(daysOfWeek, (dialog, which) -> {
                    // Update the course with the new day of the week
                    course.dayOfWeek = daysOfWeek[which];
                    notifyDataSetChanged(); // Notify the adapter that the data has changed
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateCourses(List<Course> newCourses) {
        courses = newCourses;
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView courseNameTextView;
        public TextView courseTimeTextView;
        public TextView courseDetailsTextView;
        public TextView courseDayOfWeekTextView;
        public Spinner dayOfWeekSpinner; // Spinner reference here

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            courseTimeTextView = itemView.findViewById(R.id.courseTimeTextView);
            courseDetailsTextView = itemView.findViewById(R.id.courseDetailsTextView);
            courseDayOfWeekTextView = itemView.findViewById(R.id.courseDayOfWeekTextView);
            dayOfWeekSpinner = itemView.findViewById(R.id.dayOfWeekSpinner); // Correctly find the Spinner here
        }
    }

    private int getDayOfWeekIndex(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Sunday": return 0;
            case "Monday": return 1;
            case "Tuesday": return 2;
            case "Wednesday": return 3;
            case "Thursday": return 4;
            case "Friday": return 5;
            case "Saturday": return 6;
            default: return -1; // Invalid day of the week
        }
    }
}

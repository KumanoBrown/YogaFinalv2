package com.example.yogafinalv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<Class> classes;
    private final long courseId;

    public ClassAdapter(List<Class> classes, long courseId) {
        this.classes = classes;
        this.courseId = courseId;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Class classItem, int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class classItem = classes.get(position);

        // Set text for each field
        holder.dateTextView.setText(String.format("Date: %s", classItem.date));
        holder.teacherTextView.setText(String.format("Teacher: %s", classItem.teacher));
        holder.commentTextView.setText(String.format("Comment: %s", classItem.comment));

        // Long click listener
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(classItem, holder.getAdapterPosition());
            }
            return true;
        });

        // Click listener for navigating to details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ClassDetailsActivity.class);
            intent.putExtra("classId", classItem.id);
            intent.putExtra("courseId", courseId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateClasses(List<Class> newClasses) {
        classes = newClasses;
        notifyDataSetChanged();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView teacherTextView;
        public TextView commentTextView;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            teacherTextView = itemView.findViewById(R.id.teacherTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}

package com.example.yogafinalv2;

import com.google.firebase.database.PropertyName;

public class Course {
    @PropertyName("courseId")
    public long Id;
    public int capacity;
    public int duration;
    public String description;
    public String typeofClass;
    public String dayOfWeek;
    public String timeofCourse;
    public double price;

    // Public getters and setters
    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeofClass() {
        return typeofClass;
    }

    public void setTypeofClass(String typeofClass) {
        this.typeofClass = typeofClass;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeofCourse() {
        return timeofCourse;
    }

    public void setTimeofCourse(String timeofCourse) {
        this.timeofCourse = timeofCourse;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

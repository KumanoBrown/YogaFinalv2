package com.example.yogafinalv2;

public class Class {
    private int id;
    private int courseId;
    private String name;
    private String instructor;
    private String date;
    private String time;
    private String location;

    // Default constructor
    public Class() {
    }

    // Constructor with parameters
    public Class(int id, int courseId, String name, String instructor,
                 String date, String time, String location) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.instructor = instructor;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

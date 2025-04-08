package com.gbs.gbsproject.model;

public class Course {
    private int id;
    private String name;
    private String description;
    private int tutorId;

    public Course(int id, String name, String description, int tutorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        setTutorId(tutorId);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getTutorId() { return tutorId; }

    public void setId(int id) {  this.id = id; }
    public void setName(String name) {  this.name = name; }
    public void setDescription(String description) {  this.description = description; }
    public void setTutorId(int tutorId) {  this.tutorId = tutorId; }
}
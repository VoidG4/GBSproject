package com.gbs.gbsproject.model;

public class Section {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private final int sectionOrder;

    public Section(int courseId, String title, String description, int sectionOrder) {
        setCourseId(courseId);
        this.title = title;
        this.description = description;
        this.sectionOrder = sectionOrder;
    }

    public Section(int id, int courseId, String title, String description, int sectionOrder) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.sectionOrder = sectionOrder;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSectionOrder() { return sectionOrder; }
}
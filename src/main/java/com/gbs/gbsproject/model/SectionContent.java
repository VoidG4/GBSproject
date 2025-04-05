package com.gbs.gbsproject.model;

public record SectionContent(int id, int sectionId, String title, String contentType, String content,
                             int contentOrder) {
    // Constructor, getters, setters
    public int getId(){
        return id;
    }
    public String getContent(){
        return content;
    }

    public String getTitle(){
        return title;
    }

    public String getContentType(){
        return contentType;
    }
}

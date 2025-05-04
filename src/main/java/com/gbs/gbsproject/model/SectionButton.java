package com.gbs.gbsproject.model;

import javafx.scene.control.Button;

public class SectionButton extends Button {
    private final int sectionId;

    public SectionButton(String text, int sectionId) {
        super(text); // Set button text
        this.sectionId = sectionId;
    }

    public int getSectionId() {
        return sectionId;
    }
}
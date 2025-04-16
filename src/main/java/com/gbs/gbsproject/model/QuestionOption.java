package com.gbs.gbsproject.model;

import javafx.scene.control.*;

public class QuestionOption {
    private TextField textField;
    RadioButton radioButton;
    private boolean isCorrect;

    // Getter for option text
    public String getText() {
        return textField.getText();
    }

    // Setter for the text field
    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    // Getter for whether the option is correct
    public boolean isCorrect() {
        return isCorrect;
    }

    // Setter for the correct flag
    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public void setRadioButton(RadioButton radioButton){
        this.radioButton = radioButton;
    }
}

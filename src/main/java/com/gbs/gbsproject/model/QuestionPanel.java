package com.gbs.gbsproject.model;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

public class QuestionPanel {

    private final HBox panel;
    private final TextField questionTextField;
    private final ComboBox<String> questionTypeComboBox;
    private final VBox optionsVBox;
    private final List<QuestionOption> options;
    private final TextField correctAnswerField; // For short_answer
    private String correctAnswer;
    private String correctShortAnswer;

    public QuestionPanel() {
        // Create the main panel for the question
        panel = new HBox(10);
        panel.setPrefWidth(500);
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-padding: 15px; -fx-spacing: 15px;");

        // Question Text Field (Increased width)
        questionTextField = new TextField();
        questionTextField.setPromptText("Enter Question Text");
        questionTextField.setPrefWidth(600);  // Increased width
        questionTextField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-border-color: #cccccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");
        questionTextField.setFont(Font.font("Arial", 16));

        // Question Type ComboBox
        questionTypeComboBox = new ComboBox<>();
        questionTypeComboBox.getItems().addAll("multiple_choice", "true_false", "short_answer");
        questionTypeComboBox.setValue("multiple_choice");
        questionTypeComboBox.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-border-color: #cccccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");

        // Options VBox (for holding options)
        optionsVBox = new VBox(10);
        options = new ArrayList<>();

        // For short answer question
        correctAnswerField = new TextField();
        correctAnswerField.setPromptText("Enter the correct answer");
        correctAnswerField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-border-color: #cccccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");
        correctAnswerField.setFont(Font.font("Arial", 16));

        // Update the options when the question type is changed
        questionTypeComboBox.setOnAction(_ -> updateOptions());

        // ❗️ Create the red delete button
        Button deleteButton = new Button("✕");
        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 5px;");
        deleteButton.setPrefWidth(40);
        deleteButton.setPrefHeight(40);
        deleteButton.setOnAction(_ -> ((VBox) panel.getParent()).getChildren().remove(panel));

        VBox mainContentVBox = new VBox(10, questionTextField, questionTypeComboBox, optionsVBox);
        mainContentVBox.setPrefWidth(700);

        // Add question text field and combo box to the panel
        panel.getChildren().addAll(mainContentVBox, deleteButton);

        // Initialize options
        updateOptions();
    }

    public HBox getPanel() {
        return panel;
    }

    public String getQuestionText() {
        return questionTextField.getText();
    }

    public String getQuestionType() {
        return questionTypeComboBox.getValue();
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public String getCorrectShortAnswer(){
        return correctShortAnswer;
    }

    private void updateOptions() {
        // Clear existing options and associated UI elements
        optionsVBox.getChildren().clear();
        options.clear();

        String questionType = questionTypeComboBox.getValue();

        switch (questionType) {
            case "multiple_choice" -> {
                // Create a ToggleGroup for multiple choice (only one option can be selected at a time)
                ToggleGroup toggleGroup = new ToggleGroup();

                // Create 4 options for multiple-choice
                for (int i = 0; i < 4; i++) {
                    QuestionOption option = new QuestionOption();
                    options.add(option);
                    TextField optionField = new TextField();
                    optionField.setPromptText("Option " + (i + 1));
                    optionField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-color: #cccccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");
                    optionField.setFont(Font.font("Arial", 14));

                    // RadioButton instead of Checkbox
                    RadioButton radioButton = new RadioButton("" + (i + 1));
                    radioButton.setToggleGroup(toggleGroup);
                    radioButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-text-fill: #4CAF50;");

                    option.setTextField(optionField);
                    option.setRadioButton(radioButton);

                    // Set the action for the radio button to update the correct answer
                    radioButton.setOnAction(_ -> {
                        // Set the correct answer flag to true for the selected option
                        for (QuestionOption o : options) {
                            o.setCorrect(false);  // Reset all options to false
                        }
                        option.setCorrect(true);  // Mark this option as correct
                    });

                    // Add the RadioButton and TextField to the options VBox
                    HBox optionBox = new HBox(10, optionField, radioButton);
                    optionBox.setStyle("-fx-spacing: 10px;");
                    optionsVBox.getChildren().add(optionBox);
                }
            }
            case "true_false" -> {
                // Create a ToggleGroup for True/False (only one option can be selected)
                ToggleGroup toggleGroup = new ToggleGroup();

                // Pre-made True/False options for True/False question type
                for (int i = 0; i < 2; i++) {
                    QuestionOption option = new QuestionOption();
                    options.add(option);
                    RadioButton optionRadio = getOptionRadio(i, toggleGroup, option);

                    option.setRadioButton(optionRadio);
                    optionsVBox.getChildren().add(optionRadio);
                }
            }
            case "short_answer" -> {
                // Short answer: a text field to type the correct answer
                correctAnswerField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-border-color: #cccccc; -fx-background-color: #ffffff; -fx-border-radius: 5px;");
                optionsVBox.getChildren().add(correctAnswerField);

                correctAnswerField.textProperty().addListener((_, _, newText) -> correctShortAnswer = newText);
            }
        }
    }

    @NotNull
    private RadioButton getOptionRadio(int i, ToggleGroup toggleGroup, QuestionOption option) {
        RadioButton optionRadio = new RadioButton(i == 0 ? "True" : "False");
        optionRadio.setToggleGroup(toggleGroup);
        optionRadio.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-text-fill: #4CAF50;");

        optionRadio.setOnAction(_ -> {
            // Set the correct answer flag to true for the selected option
            for (QuestionOption o : options) {
                o.setCorrect(false);  // Reset all options to false
            }

            // Update the correct answer based on the selected RadioButton
            if (optionRadio.getText().equals("True") && optionRadio.isSelected()) {
                option.setCorrect(true);  // "True" is selected as correct answer
                correctAnswer = "True"; // Assuming `questionPanel` has the setter
            } else if (optionRadio.getText().equals("False") && optionRadio.isSelected()) {
                option.setCorrect(true);  // "False" is selected as correct answer
                correctAnswer = "False";  // Assuming `questionPanel` has the setter
            }
        });
        return optionRadio;
    }
}

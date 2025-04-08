package com.gbs.gbsproject.model;

import java.util.List;

public class Question {
    private int id;
    private String questionText;
    private String type; // e.g., "multiple_choice", "true_false", "short_answer"
    private String correctAnswer;
    private List<String> options;

    public Question(){}
    // Constructor
    public Question(int id, String questionText, String type, String correctAnswer, List<String> options) {
        this.id = id;
        setQuestionText(questionText);
        setType(type);
        setCorrectAnswer(correctAnswer);
        setOptions(options);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", type='" + type + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", options=" + options +
                '}';
    }
}

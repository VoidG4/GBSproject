package com.gbs.gbsproject.dao;

public class QuestionDao {

    public int createQuestion(int quizId, String text, String questionType) {
        // SQL Query to insert question into the database
        String query = "INSERT INTO question (quiz_id, text, question_type) VALUES (?, ?, ?) RETURNING id";
        // Execute the query and return the generated question ID
        return Database.executeInsertQuery(query, quizId, text, questionType);
    }

    public void createQuestionOption(int questionId, String optionText, boolean isCorrect) {
        // SQL Query to insert question option into the database
        String query = "INSERT INTO question_option (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        // Execute the query
        Database.executeUpdateQuery(query, questionId, optionText, isCorrect);
    }
}


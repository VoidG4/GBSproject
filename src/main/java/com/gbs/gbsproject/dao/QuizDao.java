package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Question;
import com.gbs.gbsproject.model.Quiz;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizDao {
    private static final Logger LOGGER = Logger.getLogger(QuizDao.class.getName());


    public int createQuiz(int courseId, String title, String description, int passingScore) {
        // SQL Query to insert quiz into the database
        String query = "INSERT INTO quiz (course_id, title, description, passing_score) VALUES (?, ?, ?, ?) RETURNING id";
        // Execute the query and return the generated quiz ID
        return Database.executeInsertQuery(query, courseId, title, description, passingScore);
    }

    public static boolean isAnswerCorrect(int questionId, String selectedOption) throws SQLException {
        String query = "SELECT is_correct FROM question_option WHERE question_id = ? AND option_text = ?";

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setInt(1, questionId);
            stmt.setString(2, selectedOption);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_correct");
            }
        }
        return false;
    }


    public List<Quiz> getQuizzesByTutor(int tutorId) {
        List<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT q.id, q.title, q.description, q.passing_score " +
                "FROM quiz q " +
                "JOIN course c ON q.course_id = c.id " +
                "WHERE c.tutor_id = ?";  // Filter by tutor_id

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setInt(1, tutorId);  // Set the tutorId parameter

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setPassingScore(rs.getInt("passing_score"));
                    quizzes.add(quiz);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }

        return quizzes;
    }

    public void deleteQuizById(int id) throws SQLException {
        String sql = "DELETE FROM quiz WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    public List<Question> getQuestionsByQuizId(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();

        String questionQuery = "SELECT * FROM question WHERE quiz_id = ?";
        try (PreparedStatement questionStmt = DatabaseUtil.getConnection().prepareStatement(questionQuery)) {
            questionStmt.setInt(1, quizId);
            ResultSet rs = questionStmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("id");
                String questionText = rs.getString("text");
                String questionType = rs.getString("question_type");

                List<String> options = getOptionsByQuestionId(questionId);
                String correctAnswer = getCorrectAnswerByQuestionId(questionId); // optional

                Question question = new Question(questionId, questionText, questionType, correctAnswer, options);
                questions.add(question);
            }
        }

        return questions;
    }


    public List<Quiz> getQuizzesByCourseId(int courseId) {
        List<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT q.id, q.title, q.description, q.passing_score " +
                "FROM quiz q " +
                "WHERE q.course_id = ?";  // Filter by course_id

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setInt(1, courseId);  // Set the courseId parameter

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setId(rs.getInt("id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setPassingScore(rs.getInt("passing_score"));
                    quizzes.add(quiz);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }

        return quizzes;
    }


    private List<String> getOptionsByQuestionId(int questionId) throws SQLException {
        List<String> options = new ArrayList<>();
        String optionQuery = "SELECT option_text FROM question_option WHERE question_id = ?";
        try (PreparedStatement optionStmt = DatabaseUtil.getConnection().prepareStatement(optionQuery)) {
            optionStmt.setInt(1, questionId);
            ResultSet rs = optionStmt.executeQuery();
            while (rs.next()) {
                options.add(rs.getString("option_text"));
            }
        }
        return options;
    }

    public static String getCorrectAnswerByQuestionId(int questionId) throws SQLException {
        String correctAnswer = null;
        String query = "SELECT option_text FROM question_option WHERE question_id = ? AND is_correct = true";

        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setInt(1, questionId);  // Set the questionId parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    correctAnswer = rs.getString("option_text");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
            throw new SQLException("Error retrieving correct answer for question ID " + questionId, e);
        }

        return correctAnswer;
    }

}

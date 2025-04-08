package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.QuestionDao;
import com.gbs.gbsproject.dao.QuizDao;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.QuestionOption;
import com.gbs.gbsproject.model.QuestionPanel;
import com.gbs.gbsproject.model.Tutor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizViewController {
    public AnchorPane mainAnchorPane;
    public AnchorPane accountPane;
    public ScrollPane questionScrollPane;
    public Button addQuestionButton;
    public Button saveQuizButton;
    @FXML
    private TextField quizTitleField;
    @FXML
    private TextArea quizDescriptionArea;
    @FXML
    private TextField passingScoreField;
    @FXML
    private VBox questionSection;

    Course course;
    Tutor tutor;
    private static final Logger LOGGER = Logger.getLogger(QuizViewController.class.getName());

    private final List<QuestionPanel> questions = new ArrayList<>();


    public void formClicked() {
        accountPane.setVisible(false);
    }

    public void accountClicked() {
        if(accountPane.isVisible()){
            accountPane.setVisible(false);
        } else {
            accountPane.setVisible(true);
            accountPane.toFront();
        }
    }

    public void setTutor(Tutor tutor){
        this.tutor = tutor;
    }

    public void homeButtonClick() {
        try {
            // Load the FXML file
            FXMLLoader loader;
            Parent nextPage;
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            Scene nextScene;
            loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/tutor-page-view.fxml"));

            nextPage = loader.load();
            TutorPageViewController tutorController = loader.getController();
            tutorController.setTutor(tutor); // Pass Tutor object to the controller

            nextScene = new Scene(nextPage);
            // Set the stage to the previous size and position
            stage.setWidth(1600);
            stage.setHeight(1000);
            stage.centerOnScreen();

            // Set the new scene and show the stage
            stage.setScene(nextScene);
            stage.show();
        } catch (IOException e) {
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the next FXML", e);
        }
    }

    public void logOut() {
        try {
            // Load the FXML file for the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/login-page-view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the list of all windows
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No window found"));

            double width = stage.getWidth();
            double height = stage.getHeight();

            // Set the new scene with the same window size
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
        }
    }
    @FXML
    private void addQuestion() {
        // Add a new QuestionPanel to the question section
        QuestionPanel questionPanel = new QuestionPanel();
        questions.add(questionPanel);
        questionSection.getChildren().add(questionPanel.getPanel());
    }

    public void setCourse(Course course) {this.course = course;}

    @FXML
    private void saveQuiz() {
        String title = quizTitleField.getText();
        String description = quizDescriptionArea.getText();
        int passingScore = 0;

        if(isNumeric(passingScoreField.getText())) {
            passingScore = Integer.parseInt(passingScoreField.getText());
        }

        // Save the quiz to the database
        QuizDao quizDao = new QuizDao();
        int quizId = quizDao.createQuiz(course.getId(), title, description, passingScore);

        // Save questions and options
        QuestionDao questionDao = new QuestionDao();
        for (QuestionPanel questionPanel : questions) {
            int questionId = questionDao.createQuestion(quizId, questionPanel.getQuestionText(), questionPanel.getQuestionType());

            // Save options if the question is multiple choice
            if (questionPanel.getQuestionType().equals("multiple_choice")) {
                for (QuestionOption option : questionPanel.getOptions()) {
                    questionDao.createQuestionOption(questionId, option.getText(), option.isCorrect());
                }
            } else if (questionPanel.getQuestionType().equals("true_false")) {
                // Handle True/False Questions
                String[] trueFalseOptions = {"True", "False"};
                for (String optionText : trueFalseOptions) {

                    boolean isCorrect = optionText.equals(questionPanel.getCorrectAnswer());
                    questionDao.createQuestionOption(questionId, optionText, isCorrect);
                }
            } else if (questionPanel.getQuestionType().equals("short_answer")) {
                // Handle Short Answer Questions
                String correctAnswer = questionPanel.getCorrectShortAnswer();
                System.out.println("this is the correct "+correctAnswer);
                if (correctAnswer != null && !correctAnswer.isEmpty()) {
                    questionDao.createQuestionOption(questionId, correctAnswer, true);  // Save the correct answer and set it as true (correct)
                }
            }
        }

        // Feedback to the tutor
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Quiz saved successfully.");
        alert.showAndWait();
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);  // Try to parse the string as a double
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

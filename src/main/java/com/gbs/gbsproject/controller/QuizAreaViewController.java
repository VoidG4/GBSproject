package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.CourseDao;
import com.gbs.gbsproject.dao.QuizDao;
import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.*;
import com.gbs.gbsproject.service.CertificateService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizAreaViewController {
    private static final Logger LOGGER = Logger.getLogger(QuizAreaViewController.class.getName());
    static Student student;
    public Button buttonMenuStudies;
    public Button buttonMenuAccount;
    public AnchorPane accountPane;
    public AnchorPane helpPane;
    public AnchorPane studiesPane;
    public VBox courseContainer;
    public VBox quizzesContainer;
    public ScrollPane quizzesScrollPane;
    public VBox questionVBox;
    public ScrollPane questionsScrollPane;
    public AnchorPane mainAnchorPane;
    public AnchorPane passwordPane;
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public AnchorPane emailPane;
    public TextField emailField;

    @FXML
    public void initialize(){
        List<Course> courses1 = CourseDao.getAllCourses(); // fetch from DB

        for (Course course : courses1) {
            Button button = new Button(course.getName());
            button.setFont(new Font(20));
            button.setPrefSize(300, 100);
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-font-weight: bold;");
            button.setAlignment(Pos.BASELINE_LEFT);
            button.setWrapText(true);
            button.setCursor(Cursor.HAND);

            ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
            arrow.setFitHeight(32);
            arrow.setFitWidth(32);
            arrow.setPreserveRatio(true);
            button.setGraphic(arrow);
            button.setGraphicTextGap(10);

            // You can also define an action when student clicks the button
            button.setOnAction(_ -> {
                viewQuizzes(course);
                questionsScrollPane.setVisible(false);
            });

            courseContainer.getChildren().add(button);
        }
    }

    public void setStudent(Student student){
        QuizAreaViewController.student = student;
    }

    public void viewQuizzes(Course course) {
        quizzesScrollPane.setVisible(true);
        loadQuizzes(course);
    }

    private void loadQuizzes(Course course) {
        QuizDao quizDao = new QuizDao();
        List<Quiz> quizzes = quizDao.getQuizzesByCourseId(course.getId());  // Get quizzes for the tutor
        quizzesContainer.getChildren().clear();
        quizzesContainer.setAlignment(Pos.CENTER);      // Center children horizontally
        quizzesContainer.setSpacing(20);
        // For each quiz, create a button and add it to the VBox
        for (Quiz quiz : quizzes) {
            Button quizButton = new Button(quiz.getTitle());  // Use quiz title as the button label
            quizButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);
                -fx-text-fill: white;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-padding: 10 20 10 20;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                -fx-border-radius: 10;
            """);

            // Add hover effect (optional)
            quizButton.setOnMouseEntered(_ -> quizButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #43e97b, #38f9d7);
                -fx-text-fill: white;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-padding: 10 20 10 20;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);
                -fx-border-radius: 10;
            """));

            quizButton.setOnMouseExited(_ -> quizButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);
                -fx-text-fill: white;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-padding: 10 20 10 20;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);
                -fx-border-radius: 10;
            """));

            quizButton.setPrefWidth(300);

            quizButton.setOnAction(_ -> {
                try {
                    viewQuiz(quiz, quizDao.getQuestionsByQuizId(quiz.getId()), questionVBox);
                    quizzesScrollPane.setVisible(false);
                    questionsScrollPane.setVisible(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });  // When the button is clicked, show the quiz
            quizzesContainer.getChildren().add(quizButton);
        }
    }

    public void viewQuiz(Quiz quiz, List<Question> questions, VBox containerVBox) {
        containerVBox.getChildren().clear(); // Clear previous content

        Text quizTitle = new Text(quiz.getTitle());
        quizTitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        containerVBox.getChildren().add(quizTitle);

        Text quizDescription = new Text(quiz.getDescription());
        quizDescription.setStyle("-fx-font-size: 20px;");
        containerVBox.getChildren().add(quizDescription);

        Map<Integer, String> userAnswers = new HashMap<>(); // <questionId, answer>

        for (Question q : questions) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

            Label questionLabel = new Label(q.getQuestionText());
            questionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            questionBox.getChildren().add(questionLabel);

            switch (q.getType()) {
                case "multiple_choice":
                    ToggleGroup toggleGroup = new ToggleGroup();
                    for (String option : q.getOptions()) {
                        RadioButton rb = new RadioButton(option);
                        rb.setToggleGroup(toggleGroup);
                        rb.setFont(new Font(20));
                        questionBox.getChildren().add(rb);
                    }
                    // Save selected toggle for grading
                    toggleGroup.selectedToggleProperty().addListener((_, _, newVal) -> {
                        if (newVal != null) {
                            RadioButton selected = (RadioButton) newVal;
                            userAnswers.put(q.getId(), selected.getText());
                        }
                    });
                    break;

                case "true_false":
                    CheckBox trueBox = new CheckBox("True");
                    CheckBox falseBox = new CheckBox("False");
                    trueBox.setFont(new Font(20));
                    falseBox.setFont(new Font(20));

                    // Only allow one
                    trueBox.setOnAction(_ -> {
                        if (trueBox.isSelected()) {
                            falseBox.setSelected(false);
                            userAnswers.put(q.getId(), "True");
                        }
                    });

                    falseBox.setOnAction(_ -> {
                        if (falseBox.isSelected()) {
                            trueBox.setSelected(false);
                            userAnswers.put(q.getId(), "False");
                        }
                    });

                    questionBox.getChildren().addAll(trueBox, falseBox);
                    break;

                case "short_answer":
                    TextField answerField = new TextField();
                    answerField.setPromptText("Type your answer...");
                    answerField.setFont(new Font(20));

                    // Save typed answer
                    answerField.textProperty().addListener((_, _, newText) -> userAnswers.put(q.getId(), newText.trim()));

                    questionBox.getChildren().add(answerField);
                    break;
            }

            containerVBox.setSpacing(20);
            containerVBox.getChildren().add(questionBox);
        }

        // 🔥 Submit button
        Button submitBtn = getSubmitBtn(questions, userAnswers, quiz);


        containerVBox.getChildren().add(submitBtn);
    }

    @NotNull
    private static Button getSubmitBtn(List<Question> questions, Map<Integer, String> userAnswers, Quiz quiz) {
        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;");

        submitBtn.setOnAction(_ -> {

            for (Map.Entry<Integer, String> entry : userAnswers.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();

                System.out.println("Key: " + key + ", Value: " + value);
            }

            int total = questions.size();
            int correct = 0;

            for (Question q : questions) {
                String userAnswer = userAnswers.get(q.getId());

                if (userAnswer != null) {
                    try {
                        // Just check: does this option have is_correct = true?
                        boolean isCorrect = QuizDao.isAnswerCorrect(q.getId(), userAnswer);
                        if (isCorrect) correct++;
                    } catch (SQLException ex) {
                        LOGGER.log(Level.SEVERE, "An error occurred ", ex);
                    }
                }
            }

            Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
            resultAlert.setTitle("Quiz Result");
            resultAlert.setHeaderText("Your Result");
            resultAlert.setContentText("You got " + correct + " out of " + total + " correct!");
            resultAlert.showAndWait();


            int score = correct;
            StudentDao.addQuizGrade(student.getId(), quiz.getId(), score, score >= 5);
        });
        return submitBtn;
    }


    @FXML
    protected void accountClicked() {
        if(accountPane.isVisible()){
            accountPane.setVisible(false);
        } else {
            accountPane.setVisible(true);
            accountPane.toFront();
            helpPane.setVisible(false);
            studiesPane.setVisible(false);
        }
    }

    @FXML
    protected void studiesClicked() {
        if(studiesPane.isVisible()){
            studiesPane.setVisible(false);
        } else {
            studiesPane.setVisible(true);
            studiesPane.toFront();
            helpPane.setVisible(false);
            accountPane.setVisible(false);
        }
    }

    @FXML
    protected void helpClicked() {
        if(helpPane.isVisible()){
            helpPane.setVisible(false);
        } else {
            helpPane.setVisible(true);
            helpPane.toFront();
            accountPane.setVisible(false);
            studiesPane.setVisible(false);
        }
    }

    @FXML
    protected void logOut() {
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
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
            // Optionally, show a dialog to notify the user of the error
            showErrorDialog();
        }
    }

    @FXML
    protected void certificateClicked() {
        try {
            Certificate certificate = new Certificate(
                    StudentDao.getFullNameByUsername(student.getUsername()),
                    LocalDate.now()
            );

            CertificateService certificateService = new CertificateService();
            String pdfPath = certificateService.generateCertificate(certificate);

            if (pdfPath != null) {
                certificateService.openCertificate(pdfPath);
            } else {
                showErrorDialog();
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating or opening certificate", e);
            showErrorDialog();
        }
    }

    @FXML
    protected  void homeButtonClick() {
        try {
            FXMLLoader loader;
            Parent nextPage;
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            Scene nextScene;

            loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/home-page-view.fxml"));
            nextPage = loader.load();
            HomePageViewController homePageViewController = loader.getController();
            homePageViewController.setStudent(student); // Pass Student object to the controller
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
            // Optionally, show a dialog to notify the user of the error
            showErrorDialog();
        }
    }

    // Utility method to show error dialogs
    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("There was an issue opening the next page.");
        alert.showAndWait();
    }

    public void gpaClicked() {
        FXMLLoader loader;
        Parent nextPage;
        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        Scene nextScene;

        loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/gpa-view.fxml"));
        try {
            nextPage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GpaViewController gpaViewController = loader.getController();
        gpaViewController.setStudent(student); // Pass Student object to the controller
        nextScene = new Scene(nextPage);
        // Set the stage to the previous size and position
        stage.setWidth(1600);
        stage.setHeight(1000);
        stage.centerOnScreen();

        // Set the new scene and show the stage
        stage.setScene(nextScene);
        stage.show();
    }

    public void updatePassword() {
        try {
            StudentDao.updatePassword(student, newPasswordField.getText(), oldPasswordField.getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEmail() {
        try {
            StudentDao.updateEmail(student, emailField.getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void changePasswordClicked() {
        passwordPane.setVisible(true);
        accountPane.setVisible(false);
        passwordPane.toFront();
    }

    public void changeEmailClicked() {
        emailPane.setVisible(true);
        accountPane.setVisible(false);
        emailPane.toFront();
    }

    public void formClicked() {
        accountPane.setVisible(false);
        helpPane.setVisible(false);
        studiesPane.setVisible(false);
        passwordPane.setVisible(false);
        emailPane.setVisible(false);
    }
}

package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.CourseDao;
import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.Certificate;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.service.CertificateService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePageViewController {
    public AnchorPane passwordPane;
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public AnchorPane emailPane;
    public TextField emailField;
    Student student;
    private static final Logger LOGGER = Logger.getLogger(HomePageViewController.class.getName());
    public Button buttonMenuAccount;
    public AnchorPane mainAnchorPane;
    public Button buttonMenuStudies;
    @FXML
    private AnchorPane accountPane;
    @FXML
    private AnchorPane helpPane;
    @FXML
    private AnchorPane studiesPane;
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            // Now we can safely access the Stage
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();

            // Set the minimum width and height for the stage (window)
            stage.setMinWidth(1600);
            //stage.setMinHeight(790);

            // Request focus on the mainAnchorPane to remove focus from text fields
            mainAnchorPane.requestFocus();
        });
        initializeScrollPane();
    }

    @FXML
    protected void formClicked() {
        accountPane.setVisible(false);
        helpPane.setVisible(false);
        studiesPane.setVisible(false);
        passwordPane.setVisible(false);
        emailPane.setVisible(false);
    }

    public void setStudent(Student student){
        this.student = student;
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
    protected void gpaClicked() {
        try {
            FXMLLoader loader;
            Parent nextPage;
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            Scene nextScene;

            loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/gpa-view.fxml"));
            nextPage = loader.load();
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
        } catch (IOException e) {
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
            // Optionally, show a dialog to notify the user of the error
            showErrorDialog();
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

    private void initializeScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(1600);
        scrollPane.setPrefHeight(780);
        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent;");

        scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Object target = event.getTarget();

            if (target instanceof javafx.scene.control.ScrollBar) {
                // Allow scrollbar interaction
                return;
            }

            // Allow interaction with actual controls
            if (target instanceof Button || target instanceof Label || target instanceof TextField || target instanceof ProgressBar) {
                return;
            }

            // Otherwise consume the event to block background-based scrolling
            event.consume();
        });




        AnchorPane.setTopAnchor(scrollPane, 180.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);

        AnchorPane contentPane = new AnchorPane();
        contentPane.setMinHeight(1000); // Ensure enough height for scrolling
        contentPane.setStyle("-fx-background-color: transparent;");

        Label coursesLabel = new Label("COURSES");
        coursesLabel.setFont(new Font("Arial", 30));
        coursesLabel.setStyle("-fx-font-weight: bold;-fx-background-color: transparent; -fx-text-fill: black;");
        coursesLabel.setAlignment(Pos.CENTER);

        Label AITutorLabel = new Label("AI TUTOR");
        AITutorLabel.setFont(new Font("Arial", 30));
        AITutorLabel.setStyle("-fx-font-weight: bold;-fx-background-color: transparent;-fx-text-fill: black;");
        AITutorLabel.setAlignment(Pos.CENTER);

        Label quizLabel = new Label("QUIZ AREA");
        quizLabel.setFont(new Font("Arial", 30));
        quizLabel.setStyle("-fx-font-weight: bold;-fx-background-color: transparent; -fx-text-fill: black;");
        quizLabel.setAlignment(Pos.CENTER);

        ImageView courses = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/online-course.png"))));
        courses.setFitHeight(40);
        courses.setFitWidth(40);
        courses.setPreserveRatio(true);

        ImageView aiTutor = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/generative.png"))));
        aiTutor.setFitHeight(45);
        aiTutor.setFitWidth(45);
        aiTutor.setPreserveRatio(true);

        ImageView quiz = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/quiz.png"))));
        quiz.setFitHeight(45);
        quiz.setFitWidth(45);
        quiz.setPreserveRatio(true);

        AnchorPane.setTopAnchor(coursesLabel, 50.0);
        AnchorPane.setLeftAnchor(coursesLabel, 250.0);

        AnchorPane.setTopAnchor(courses, 50.0);
        AnchorPane.setLeftAnchor(courses, 200.0);

        AnchorPane aiTutorSection = new AnchorPane();
        aiTutorSection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 10;");

        Label aiTutorTitleLabel = new Label("Welcome to the AI Tutor");
        aiTutorTitleLabel.setFont(new Font("Arial", 24));
        aiTutorTitleLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: black;");

        Label aiTutorDescriptionLabel = getAiTutorDescriptionLabel();

        Button beginWithAITutorButton = getButton();

        AnchorPane.setTopAnchor(aiTutorTitleLabel, 20.0);
        AnchorPane.setLeftAnchor(aiTutorTitleLabel, 20.0);

        AnchorPane.setTopAnchor(aiTutorDescriptionLabel, 60.0);
        AnchorPane.setLeftAnchor(aiTutorDescriptionLabel, 20.0);

        AnchorPane.setTopAnchor(beginWithAITutorButton, 140.0);
        AnchorPane.setLeftAnchor(beginWithAITutorButton, 20.0);

        aiTutorSection.getChildren().addAll(aiTutorTitleLabel, aiTutorDescriptionLabel, beginWithAITutorButton);
        aiTutorSection.prefWidthProperty().bind(contentPane.widthProperty().subtract(400));


        AnchorPane quizPane = new AnchorPane();
        quizPane.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 10;");

        Label titleLabel = new Label("Test");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: black;");

        Label descriptionLabel = getQuizDescriptionLabel();

        Button actionButton = getActionButton();

        AnchorPane.setTopAnchor(titleLabel, 20.0);
        AnchorPane.setLeftAnchor(titleLabel, 20.0);

        AnchorPane.setTopAnchor(descriptionLabel, 60.0);
        AnchorPane.setLeftAnchor(descriptionLabel, 20.0);

        AnchorPane.setTopAnchor(actionButton, 140.0);
        AnchorPane.setLeftAnchor(actionButton, 20.0);
        quizPane.getChildren().addAll(titleLabel, descriptionLabel, actionButton);
        quizPane.prefWidthProperty().bind(contentPane.widthProperty().subtract(400));


        FlowPane buttonContainer = new FlowPane(10, 10);
        buttonContainer.setPrefWrapLength(1000);
        buttonContainer.setAlignment(Pos.TOP_LEFT);
        buttonContainer.setStyle("-fx-background-color: transparent;");

        double buttonWidth = 385;
        double buttonHeight = 100;

        List<Course> courses1 = CourseDao.getAllCourses(); // fetch from DB

        int i = 0;
        for (Course course : courses1) {
            i+=1;
            Button button = new Button(course.getName());
            button.setFont(new Font(20));
            button.setPrefSize(buttonWidth, buttonHeight);
            button.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-font-weight: bold;");
            button.setAlignment(Pos.BASELINE_LEFT);
            button.setWrapText(true);

            ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
            arrow.setFitHeight(32);
            arrow.setFitWidth(32);
            arrow.setPreserveRatio(true);
            button.setGraphic(arrow);
            button.setGraphicTextGap(10);

            button.setOnMouseEntered(_ -> {
                button.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
                button.setCursor(Cursor.HAND);
            });

            button.setOnMouseExited(_ -> {
                button.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color:  lightgray;-fx-font-weight: bold;");
                button.setCursor(Cursor.DEFAULT);
            });

            // You can also define an action when student clicks the button
            button.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/course-view.fxml"));
                    Parent root = loader.load();

                    // Pass the course to the controller
                    CourseViewController controller = loader.getController();
                    controller.setCourse(course); // Custom method to receive course info
                    controller.setStudent(student);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    double width = stage.getWidth();
                    double height = stage.getHeight();

                    // Set the new scene
                    Scene scene = new Scene(root, width, height);
                    stage.setScene(scene);

                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.show();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "An error occurred ", e);
                }
            });

            buttonContainer.getChildren().add(button);
        }

        double topAnchor = 100.0;
        double buttonContainerHeight = buttonHeight*(i-1)/3;
        AnchorPane.setTopAnchor(buttonContainer, topAnchor);
        AnchorPane.setLeftAnchor(buttonContainer, 150.0);
        AnchorPane.setRightAnchor(buttonContainer, 250.0);

        AnchorPane.setTopAnchor(AITutorLabel, topAnchor + buttonContainerHeight + 100);
        AnchorPane.setLeftAnchor(AITutorLabel, 235.0);

        AnchorPane.setTopAnchor(aiTutor, topAnchor + buttonContainerHeight+ 100);
        AnchorPane.setLeftAnchor(aiTutor, 185.0);

        AnchorPane.setTopAnchor(aiTutorSection, topAnchor + buttonContainerHeight+ 150);
        AnchorPane.setLeftAnchor(aiTutorSection, 150.0);

        AnchorPane.setTopAnchor(quizLabel, topAnchor + buttonContainerHeight + 450);
        AnchorPane.setLeftAnchor(quizLabel, 235.0);

        AnchorPane.setTopAnchor(quiz, topAnchor + buttonContainerHeight+ 450);
        AnchorPane.setLeftAnchor(quiz, 185.0);

        AnchorPane.setTopAnchor(quizPane, topAnchor + buttonContainerHeight+ 500);
        AnchorPane.setLeftAnchor(quizPane, 150.0);

        contentPane.getChildren().addAll(courses, coursesLabel, buttonContainer, AITutorLabel, aiTutor, aiTutorSection, quizLabel, quizPane, quiz);
        scrollPane.setContent(contentPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure vertical scroll bar

        mainAnchorPane.getChildren().add(scrollPane);
    }

    @NotNull
    private Button getActionButton() {
        Button actionButton = new Button("Begin Now");
        actionButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        actionButton.setFont(new Font("Arial", 18));
        actionButton.setPrefSize(200, 50);
        actionButton.setAlignment(Pos.CENTER);
        actionButton.setOnMouseClicked(_ -> {
            try {
                FXMLLoader loader;
                Parent nextPage;
                Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
                Scene nextScene;

                loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/quiz-area-view.fxml"));
                nextPage = loader.load();
                QuizAreaViewController quizAreaViewController = loader.getController();
                quizAreaViewController.setStudent(student); // Pass Student object to the controller
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
                LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
                // Optionally, show a dialog to notify the user of the error
                showErrorDialog();
            }
        });
        return actionButton;
    }

    @NotNull
    private Button getButton() {
        Button beginWithAITutorButton = new Button("Begin with AI Tutor");
        beginWithAITutorButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        beginWithAITutorButton.setFont(new Font("Arial", 18));
        beginWithAITutorButton.setPrefSize(200, 50);
        beginWithAITutorButton.setAlignment(Pos.CENTER);
        beginWithAITutorButton.setOnMouseClicked(_ -> {
            try {

                FXMLLoader loader;
                Parent nextPage;
                Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
                Scene nextScene;

                loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/gemini-view.fxml"));
                nextPage = loader.load();
                GeminiController geminiController = loader.getController();
                geminiController.setStudent(student); // Pass Student object to the controller
                nextScene = new Scene(nextPage);

                stage.centerOnScreen();

                // Set the new scene and show the stage
                stage.setScene(nextScene);
                stage.show();

            } catch (IOException e) {
                // Log the exception using a logger instead of printStackTrace()
                LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
                // Optionally, show a dialog to notify the user of the error
                showErrorDialog();
            }
        });
        return beginWithAITutorButton;
    }

    @NotNull
    private static Label getAiTutorDescriptionLabel() {
        Label aiTutorDescriptionLabel = new Label("AI Tutor provides personalized learning experiences at Greece Business School, offering support in various subjects and helping you enhance your skills at your own pace.");
        aiTutorDescriptionLabel.setFont(new Font("Arial", 18)); // Medium font size
        aiTutorDescriptionLabel.setWrapText(true); // Wrap text if it's too long
        aiTutorDescriptionLabel.setMaxWidth(600); // Set max width for the label
        aiTutorDescriptionLabel.setStyle("-fx-text-fill: #333;");
        return aiTutorDescriptionLabel;
    }

    @NotNull
    private static Label getQuizDescriptionLabel() {
        Label descriptionLabel = new Label("Test your knowledge and never miss out on an exciting opportunity to learn and grow! Learn with the latest quizzes right here!");
        descriptionLabel.setFont(new Font("Arial", 18));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(600);
        descriptionLabel.setStyle("-fx-text-fill: #333;");
        return descriptionLabel;
    }

    public void updatePassword() {
        try {
            StudentDao.updatePassword(student, newPasswordField.getText(), oldPasswordField.getText());
            passwordPane.setVisible(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEmail() {
        try {
            StudentDao.updateEmail(student, emailField.getText());
            emailPane.setVisible(false);
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
}
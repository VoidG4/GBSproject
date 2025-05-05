package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.*;
import com.gbs.gbsproject.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorPageViewController {
    public TextField nameTextField;
    public TextArea textAreaDescription;
    public AnchorPane coursePane;
    public ScrollPane scrollPane;
    public AnchorPane vboxCourses;
    public Label courseName;
    public AnchorPane modifyPane;
    public TextField titleField;
    public ChoiceBox<String> choice;
    public Label sectionLabel;
    public TextArea contentArea;
    public ScrollPane sectionScroll;
    public AnchorPane contentPane;
    public ScrollPane contentScroll;
    public Button secButton;
    public AnchorPane contentSecPane;
    public AnchorPane viewScroll;
    public ScrollPane viewScrollPane;
    public AnchorPane content;
    public AnchorPane mainAnchorPane;
    public VBox questionVBox;
    public ScrollPane quizzesScrollPane;
    public ScrollPane quizzesScroll;
    public TextField newPasswordField;
    public TextField oldPasswordField;
    public AnchorPane passwordPane;
    public AnchorPane emailPane;
    public TextField emailField;
    private Button currentSectionContentButton;
    @FXML
    private VBox quizListVBox;

    @FXML
    private VBox contentVBox;
    Tutor tutor;
    Course currentCourse;
    private final List<Section> sections = new ArrayList<>();
    private Section currentSection; // Set this when selecting the section
    private Button currentSectionButton;
    private int contentOrder = 1; // You can count from DB or increase locally
    private SectionContent currentSectionContent;

    private static final Logger LOGGER = Logger.getLogger(TutorPageViewController.class.getName());

    public AnchorPane accountPane;

    private final CourseDao courseDAO = new CourseDao();

    public void initialize() {
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        //scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent;");

        contentVBox.setSpacing(20);
        contentVBox.setPadding(new Insets(20));
        contentVBox.setPrefWidth(800); // Optional: set preferred width
        content.getChildren().clear();
        content.getChildren().add(contentVBox);

        AnchorPane.setTopAnchor(contentVBox, 0.0);
        AnchorPane.setLeftAnchor(contentVBox, 0.0);
        AnchorPane.setRightAnchor(contentVBox, 0.0);


        // Adding items/choices to the ChoiceBox
        choice.getItems().addAll("text", "video", "image");
        choice.setValue("text");
        choice.setStyle("-fx-font-size: 20px; -fx-background-color: white; -fx-border-color: gray;");
    }

    public void formClicked() {
        accountPane.setVisible(false);
        contentScroll.setVisible(false);
        emailPane.setVisible(false);
        passwordPane.setVisible(false);
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

    private void loadQuizzes() {
        QuizDao quizDao = new QuizDao();
        List<Quiz> quizzes = quizDao.getQuizzesByTutor(tutor.getId());  // Get quizzes for the tutor
        quizListVBox.getChildren().clear();
        quizListVBox.setAlignment(Pos.CENTER);      // Center children horizontally
        quizListVBox.setSpacing(20);
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
                    quizzesScroll.setVisible(true);
                    quizzesScrollPane.setVisible(false);
                    scrollPane.setVisible(false);
                    sectionScroll.setVisible(false);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });  // When the button is clicked, show the quiz
            quizListVBox.getChildren().add(quizButton);
        }
    }

    public void viewQuiz(Quiz quiz, List<Question> questions, VBox containerVBox) {
        containerVBox.getChildren().clear();

        Text quizTitle = new Text(quiz.getTitle());
        quizTitle.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        containerVBox.getChildren().add(quizTitle);

        Text quizDescription = new Text(quiz.getDescription());
        quizDescription.setStyle("-fx-font-size: 20px;");
        containerVBox.getChildren().add(quizDescription);

        Map<Question, Object> userAnswers = new HashMap<>();

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
                        rb.setFont(new Font(20));
                        rb.setToggleGroup(toggleGroup);
                        questionBox.getChildren().add(rb);

                        // Save selected answer
                        rb.setOnAction(_ -> userAnswers.put(q, option));
                    }
                    break;

                case "true_false":
                    ToggleGroup tfGroup = new ToggleGroup();
                    RadioButton trueBtn = new RadioButton("True");
                    RadioButton falseBtn = new RadioButton("False");

                    trueBtn.setFont(new Font(20));
                    falseBtn.setFont(new Font(20));

                    trueBtn.setToggleGroup(tfGroup);
                    falseBtn.setToggleGroup(tfGroup);

                    questionBox.getChildren().addAll(trueBtn, falseBtn);

                    trueBtn.setOnAction(_ -> userAnswers.put(q, "True"));
                    falseBtn.setOnAction(_ -> userAnswers.put(q, "False"));
                    break;

                case "short_answer":
                    TextField answerField = new TextField();
                    answerField.setPromptText("Type your answer...");
                    answerField.setFont(new Font(20));
                    questionBox.getChildren().add(answerField);

                    answerField.textProperty().addListener((_, _, newText) -> userAnswers.put(q, newText.trim()));
                    break;
            }

            containerVBox.getChildren().add(questionBox);
        }

        // ✅ Submit Button
        Button submitButton = getSubmitButton(questions, userAnswers);

        containerVBox.setSpacing(20);
        containerVBox.getChildren().add(submitButton);
    }

    @NotNull
    private static Button getSubmitButton(List<Question> questions, Map<Question, Object> userAnswers) {
        Button submitButton = new Button("Submit Quiz");
        submitButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 18px;");

        submitButton.setOnAction(_ -> {
            int correctCount = 0;

            for (Question q : questions) {
                Object userAnswer = userAnswers.get(q);
                if (userAnswer == null) continue;

                String correctAnswer = q.getCorrectAnswer().trim();
                String userInput = userAnswer.toString().trim();

                if (q.getType().equals("short_answer")) {
                    if (userInput.equalsIgnoreCase(correctAnswer)) {
                        correctCount++;
                    }
                } else {
                    if (userInput.equals(correctAnswer)) {
                        correctCount++;
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Quiz Result");
            alert.setHeaderText("You got " + correctCount + " out of " + questions.size() + " correct!");
            alert.showAndWait();
        });
        return submitButton;
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

    public void addCourse(){

        String name = nameTextField.getText();
        String description = textAreaDescription.getText();
        int tutorId = tutor.getId();

        // Generate a unique ID for the new course (or handle the ID assignment logic)
        int id = generateCourseId(); // You would implement this based on your logic (e.g., auto-increment, or next available ID)

        // Create the course object
        Course course = new Course(id, name, description, tutorId);

        if (name.isEmpty()) {
            nameTextField.setStyle("-fx-border-color : red");
        } else {
            // Try to insert the course using the DAO
            boolean isInserted = courseDAO.insertCourse(course);

            // Show alert based on the result
            if (isInserted) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Course added successfully.");
                coursePane.setVisible(false);
                nameTextField.clear();
                textAreaDescription.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the course.");
            }
        }
    }

    public void addCourseClicked() {
        coursePane.setVisible(true);
        scrollPane.setVisible(false);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(false);
        quizzesScroll.setVisible(false);
    }

    // You might want to implement a method to generate the course ID.
    public int generateCourseId() {
        Random random = new Random();
        int courseId;
        boolean isUnique;

        do {
            // Generate a random 3-digit number (between 100 and 999)
            courseId = 100 + random.nextInt(900);
            isUnique = courseDAO.checkIfCourseIdExists(courseId);
        } while (!isUnique);

        return courseId;
    }

    // Utility method for showing alerts
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void displayTutorCourses() {
        coursePane.setVisible(false);
        scrollPane.setVisible(true);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(false);
        quizzesScroll.setVisible(false);

        List<Course> courses = CourseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseButton(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();
    }

    @NotNull
    private Button getCourseButton(List<Course> courses, int i) {
        double buttonWidth = 500;
        double buttonHeight = 100;

        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setFont(new Font(20));
        courseButton.setPrefSize(buttonWidth, buttonHeight);
        courseButton.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-font-weight: bold;");
        courseButton.setAlignment(Pos.BASELINE_LEFT);
        courseButton.setWrapText(true);

        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
        arrow.setFitHeight(32);
        arrow.setFitWidth(32);
        arrow.setPreserveRatio(true);
        courseButton.setGraphic(arrow);
        courseButton.setGraphicTextGap(10);
        courseButton.setOnMouseEntered(_ -> {
            courseButton.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.HAND);
        });

        courseButton.setOnMouseExited(_ -> {
            courseButton.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color:  lightgray;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.DEFAULT);
        });

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 110 + 20); // Set vertical position with spacing between buttons

        // Add an event handler to handle clicks on the button
        courseButton.setOnAction(_ -> {
            scrollPane.setVisible(false);
            sectionScroll.setVisible(true);
            courseName.setText(course.getName());
            currentCourse = course;
            displaySectionsForCourse(course);
        });
        return courseButton;
    }

    @NotNull
    private Button getCourseButtonDelete(List<Course> courses, int i) {
        double buttonWidth = 500;
        double buttonHeight = 100;

        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setFont(new Font(20));
        courseButton.setPrefSize(buttonWidth, buttonHeight);
        courseButton.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-font-weight: bold;");
        courseButton.setAlignment(Pos.BASELINE_LEFT);
        courseButton.setWrapText(true);

        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
        arrow.setFitHeight(32);
        arrow.setFitWidth(32);
        arrow.setPreserveRatio(true);
        courseButton.setGraphic(arrow);
        courseButton.setGraphicTextGap(10);
        courseButton.setOnMouseEntered(_ -> {
            courseButton.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.HAND);
        });

        courseButton.setOnMouseExited(_ -> {
            courseButton.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.DEFAULT);
        });

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 110 + 20); // Set vertical position with spacing between buttons

        // Add an event handler to handle clicks on the button
        courseButton.setOnAction(_ -> {
            // Create a confirmation dialog
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Course");
            confirmationAlert.setHeaderText("Are you sure you want to delete this course?");
            confirmationAlert.setContentText("Once deleted, the course cannot be recovered.");

            // Show the confirmation dialog and wait for the user's response
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Proceed to delete the course from the database
                    boolean success = courseDAO.deleteCourse(course.getId());

                    if (success) {
                        // Refresh the course list and update UI
                        Platform.runLater(() -> {
                            // Remove the button from the container
                            vboxCourses.getChildren().remove(courseButton);

                            refreshCourseList(vboxCourses);
                        });
                    } else {
                        System.out.println("Error deleting the course from the database.");
                    }
                } else {
                    System.out.println("Course deletion cancelled.");
                }
            });
        });

        return courseButton;
    }

    private void refreshCourseList(AnchorPane buttonContainer) {
        // First clear the existing buttons
        buttonContainer.getChildren().clear();

        // Fetch updated list of courses from the database
        List<Course> updatedCourses = CourseDao.getCoursesByTutorId(tutor.getId());

        // Add buttons for the remaining courses
        for (int i = 0; i < updatedCourses.size(); i++) {
            Button courseButton = getCourseButtonDelete(updatedCourses, i);
            buttonContainer.getChildren().add(courseButton);
        }
    }

    public void displaySectionsForCourse(Course course) {
        try {
            // Clear previous buttons
            modifyPane.getChildren().clear();

            // Load all sections for this course
            List<Section> sectionList = SectionDao.getSectionsForCourse(course.getId());

            double yOffset = 10; // starting vertical position

            for (Section section : sectionList) {
                Button sectionButton = getSectionButton(section, yOffset);

                modifyPane.getChildren().add(sectionButton);
                yOffset += sectionButton.getPrefHeight() + 20;
            }

            secButton.setText("section(+)");
            secButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;-fx-background-color: #00CC00; -fx-text-fill: white;-fx-font-weight: bold;");
            secButton.setPrefWidth(320);
            secButton.setPrefHeight(40);
            secButton.setLayoutX(10);
            secButton.setLayoutY(yOffset);
            modifyPane.getChildren().add(secButton);

            secButton.setOnMouseClicked(this::addNewSection);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }

    @NotNull
    private Button getSectionButton(Section section, double yOffset) {
        Button sectionButton = new Button(section.getTitle());

        // Apply the same style as clickedButton
        sectionButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;-fx-background-color: white; -fx-border-color: gray; -fx-text-fill: gray; -fx-alignment: CENTER_LEFT;");
        sectionButton.setPrefWidth(320); // adjust as needed
        sectionButton.setPrefHeight(40); // adjust as needed

        sectionButton.setLayoutX(10); // or wherever you want it on X
        sectionButton.setLayoutY(yOffset);

        sectionButton.setOnMouseClicked(_ -> {
            sectionLabel.setText("Section: " + section.getTitle());
            titleField.setText(section.getTitle());
            contentArea.setText(section.getDescription());
            currentSection = section;
            currentSectionButton = sectionButton;
            // Optional: also load the contents here if you'd like
            List<Button> buttons = displaySectionContents(section.getId());

            contentScroll.setVisible(!buttons.isEmpty());
        });
        return sectionButton;
    }

    @NotNull
    private Button getCourseViewButton(List<Course> courses, int i) {
        double buttonWidth = 500;
        double buttonHeight = 100;

        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setFont(new Font(20));
        courseButton.setPrefSize(buttonWidth, buttonHeight);
        courseButton.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-font-weight: bold;");
        courseButton.setAlignment(Pos.BASELINE_LEFT);
        courseButton.setWrapText(true);

        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
        arrow.setFitHeight(32);
        arrow.setFitWidth(32);
        arrow.setPreserveRatio(true);
        courseButton.setGraphic(arrow);
        courseButton.setGraphicTextGap(10);
        courseButton.setOnMouseEntered(_ -> {
            courseButton.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.HAND);
        });

        courseButton.setOnMouseExited(_ -> {
            courseButton.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color:  lightgray;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.DEFAULT);
        });

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 110 + 20); // Set vertical position with spacing between buttons

        courseButton.setOnAction(_ -> {
            // Clear previous section buttons and content before displaying new ones
            content.getChildren().clear();
            viewScroll.getChildren().clear();
            viewScrollPane.setVisible(true);

            // Fetch the sections for the selected course (you can use a method like getSectionsByCourseId(courseId))
            List<Section> sections;
            try {
                sections = SectionDao.getSectionsForCourse(course.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Label titleLabel = new Label(course.getName()); // Set the course title
            titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;"); // Larger font size for the title
            titleLabel.setWrapText(true);  // Allow the title to wrap if it's too long
            titleLabel.setMaxWidth(700);   // Set maximum width for the title

            // Create a TextFlow for the description with text styled at 20px
            TextFlow descriptionFlow = getTextFlow(course);

            // Add the title label and description flow to the VBox
            contentVBox.getChildren().clear();
            contentVBox.getChildren().addAll(titleLabel, descriptionFlow);
            content.getChildren().add(contentVBox);

            double currentY = 20; // Starting Y position
            for (Section section : sections) {
                Button sectionButton = getNewButton(section, currentY);

                // Add the section button to the left AnchorPane (sectionButtonsPane)
                viewScroll.getChildren().add(sectionButton);

                // Increase Y position for next section button
                currentY += sectionButton.getHeight() + 100; // Add some space between buttons
            }
        });

        return courseButton;
    }

    @NotNull
    private static TextFlow getTextFlow(Course course) {
        Text descriptionText = new Text(course.getDescription());  // Use the course description
        descriptionText.setStyle("-fx-font-size: 20px;"); // Set font size for the description

        // Create a TextFlow to wrap the description text
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-border-color: lightgray;");
        descriptionFlow.setMaxWidth(700);  // Ensure it wraps properly within the max width
        return descriptionFlow;
    }

    @NotNull
    private Button getNewButton(Section section, double currentY) {
        Button sectionButton = new Button(section.getTitle()); // Section title as button text
        sectionButton.setPrefWidth(230); // Set width of section buttons
        sectionButton.setPrefHeight(100); // Set height of section buttons
        sectionButton.setWrapText(true);

        sectionButton.setStyle("-fx-background-color: #168fff; -fx-text-fill: white; -fx-border-color: lightgray; -fx-font-weight: bold;-fx-font-size: 20px;");
        sectionButton.setLayoutX(20); // Position the button on the left side
        sectionButton.setLayoutY(currentY);
        sectionButton.setOnAction(_ -> {
            // When a section button is clicked, fetch and display the content for that section
            content.getChildren().clear();
            displaySectionContents(section, contentVBox);

            content.getChildren().add(contentVBox);
            // Dynamically update the height of viewScroll based on contentVBox height
            updateAnchorPaneHeight(viewScroll);

        });
        return sectionButton;
    }

    private void updateAnchorPaneHeight(AnchorPane viewScroll) {
        viewScroll.setPrefHeight(3500);  // Add some extra space (optional)
    }

    // Method to display the section contents in the content area (Right side)
    private void displaySectionContents(Section section, VBox contentVBox) {
        contentVBox.getChildren().clear();

        List<SectionContent> sectionContents;
        try {
            sectionContents = SectionContentDao.getContentBySectionId(section.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SectionContent content : sectionContents) {
            // Title
            Label titleLabel = new Label(content.getTitle());
            titleLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
            titleLabel.setWrapText(true);
            titleLabel.setMaxWidth(700);

            contentVBox.getChildren().add(titleLabel);

            switch (content.getContentType()) {
                case "text" -> {
                    Text text = new Text(content.getContent());
                    text.setStyle("-fx-font-size: 20px;");
                    text.setWrappingWidth(700);

                    TextFlow textFlow = new TextFlow(text);
                    textFlow.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-border-color: lightgray;");
                    textFlow.setMaxWidth(700);  // Ensure it wraps properly within the max width
                    contentVBox.getChildren().add(textFlow);
                }
                case "video" -> {
                    // If the content type is video, display it as a clickable link
                    String youtubeUrl = content.getContent();

                    // Create a Hyperlink for the YouTube video URL
                    Hyperlink videoLink = new Hyperlink("Watch video: " + youtubeUrl);
                    videoLink.setStyle("-fx-font-size: 20px; -fx-text-fill: blue;");

                    // When clicked, open the video in the default web browser
                    videoLink.setOnAction(_ -> openLinkInBrowser(youtubeUrl));

                    contentVBox.getChildren().add(videoLink);
                }
                case "image" -> {
                    try {
                        Image image = new Image(content.getContent(), true); // 'true' loads in background
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(700); // Adjust as needed
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        contentVBox.getChildren().add(imageView);
                    } catch (Exception ex) {
                        Label errorLabel = new Label("Failed to load image.");
                        errorLabel.setStyle("-fx-text-fill: red;");
                        contentVBox.getChildren().add(errorLabel);
                    }
                }
            }
        }
        updateAnchorPaneHeight(viewScroll);
    }

    private void openLinkInBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                // Windows command to open URL in default browser
                processBuilder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            } else if (os.contains("mac")) {
                // MacOS command to open URL in default browser
                processBuilder = new ProcessBuilder("open", url);
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux/Unix command to open URL in default browser
                processBuilder = new ProcessBuilder("xdg-open", url);
            } else {
                System.out.println("Unsupported OS for opening URL.");
                return;
            }

            // Start the process and wait for it to execute
            processBuilder.start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }


    public void addNewSection(MouseEvent event) {
        // Get the clicked button (the "Add New Section" button)
        Button clickedButton = (Button) event.getSource();

        // Change the text of the clicked button to the title from the text field
        clickedButton.setText(titleField.getText()); // Set button text to the title from the text field

        // Get the course ID (you will need to pass this information, or get it from somewhere)
        int courseId = currentCourse.getId(); // Replace with the actual course ID (use the right variable or context)

        // Get the section order (you can use the size of the sections list to determine the order)
        int sectionOrder = sections.size() + 1;

        // Create a new section
        Section newSection = new Section(courseId, titleField.getText(), "Description", sectionOrder);
        try {
            newSection = SectionDao.insertSection(newSection); // Save the section in the database
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        titleField.setText(""); // Clear the text field after setting the title
        sections.add(newSection); // Add the section to the list

        // Create a new "Add New Section" button for the next section
        Button newSectionButton = getNewSectionButton(clickedButton);

        // Set the event handler for the current button to display the section info when clicked
        Section finalNewSection = newSection;
        clickedButton.setOnMouseClicked(_ -> {
            // Display the section info, here we are just printing the section title and content
            sectionLabel.setText("Section: " + finalNewSection.getTitle());
            currentSection = finalNewSection;

            // Now load and display all the contents for the clicked section
            List<Button> buttons = displaySectionContents(finalNewSection.getId());
            contentScroll.setVisible(!buttons.isEmpty());
        });

        // Add the new button to the layout (e.g., AnchorPane or any layout you're using)
        modifyPane.getChildren().add(newSectionButton); // 'modifyPane' is your layout, like AnchorPane or VBox
    }


    // Method to display all contents of a section
    private List<Button> displaySectionContents(int sectionId) {
        // Get the section contents from the database based on the section ID
        List<SectionContent> sectionContents;
        List<Button> buttons = new ArrayList<>();
        try {
            sectionContents = SectionContentDao.getContentsForSection(sectionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Clear any previous content in the layout
        contentPane.getChildren().clear(); // Assuming you have a content pane to display the section contents

        double currentY = 10; // Starting Y position (space from top)

        // Add each content as a TextArea or Label to the content pane
        for (SectionContent content1 : sectionContents) {
            Button contentButton = new Button(content1.getTitle()); // Set the button's title to the content's title

            // Optionally, style the Button
            contentButton.setStyle("-fx-padding: 10px; -fx-font-size: 14px; -fx-border-color: gray;");
            contentButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;-fx-background-color: white; -fx-border-color: gray; -fx-text-fill: gray; -fx-alignment: CENTER_LEFT;");
            contentButton.setPrefWidth(200);
            // Set the position of the button (with spacing)
            contentButton.setLayoutY(currentY);
            buttons.add(contentButton);
            // Add the button to the content pane
            contentPane.getChildren().add(contentButton);

            // When the button is clicked, set the content in the mainTextArea
            contentButton.setOnAction(_ -> {
                titleField.setText(content1.getTitle());
                contentArea.setText(content1.getContent()); // Display the content in the mainTextArea
                choice.setValue(content1.getContentType());

                contentScroll.setVisible(false);
                currentSectionContent = content1;
                currentSectionContentButton = contentButton;
            });

            // Increment the Y position for the next button, including some spacing
            currentY += contentButton.getHeight() + 50; // Add 10px of space between buttons
        }

        return buttons;
    }

    @NotNull
    private Button getNewSectionButton(Button clickedButton) {
        Button newSectionButton = new Button("section(+)");

        // Set the same size for the new button as the clicked button
        newSectionButton.setPrefWidth(clickedButton.getPrefWidth());
        newSectionButton.setPrefHeight(clickedButton.getPrefHeight());

        clickedButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;-fx-background-color: white; -fx-border-color: gray; -fx-text-fill: gray; -fx-alignment: CENTER_LEFT;");
        // Optional: Style the new button (if needed)
        newSectionButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;-fx-background-color: #00CC00; -fx-text-fill: white;-fx-font-weight: bold;");

        // Position the new button below the clicked button
        double clickedButtonY = clickedButton.getLayoutY(); // Get the Y position of the clicked button
        newSectionButton.setLayoutX(clickedButton.getLayoutX()); // Set X position same as clicked button
        newSectionButton.setLayoutY(clickedButtonY + clickedButton.getHeight() + 10); // Y is below the clicked button with some padding

        // Set the event handler for the new "Add New Section" button
        newSectionButton.setOnMouseClicked(this::addNewSection); // The same handler as the original button
        return newSectionButton;
    }


    public void addContent() {
        String type = choice.getValue();
        String content = contentArea.getText();
        String title = titleField.getText();

        if (type == null || content.isEmpty() || title.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        SectionContentDao dao = new SectionContentDao();
        dao.insertSectionContent(currentSection.getId(), title, type, content, contentOrder++);

        titleField.clear();
        contentArea.clear();
        choice.setValue("text");
    }

    public void viewCourses() {
        coursePane.setVisible(false);
        scrollPane.setVisible(true);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(false);
        quizzesScroll.setVisible(false);

        List<Course> courses = CourseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseViewButton(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();
    }

    public void deleteClicked() {
        try {
            // Check if there is content displayed in the mainContentArea
            String contentToDelete = titleField.getText();

            if (contentToDelete.isEmpty()) {
                System.out.println("No content to delete.");
                return;
            }

            // Find the SectionContent object (you might have it stored somewhere in the UI)
            SectionContent content = SectionContentDao.getContentBySectionIdAndTitle(currentSection.getId(), contentToDelete);

            if (content == null) {
                System.out.println("Content not found.");
                return;
            }
            contentScroll.setVisible(false);


            // Call the DAO to delete the content
            SectionContentDao.deleteSectionContent(content.getId());

            // Optionally, clear the TextArea after deletion
            contentArea.clear();
            titleField.clear();
            contentScroll.setVisible(false);
            contentSecPane.getChildren().remove(currentSectionContentButton);


            // Remove the corresponding button from the UI (if needed)
            removeContentButton(content);

            System.out.println("Content deleted successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);

        }
    }

    private void removeContentButton(SectionContent content) {
        // Loop through content buttons and find the one that matches the content's title
        for (Node node : contentPane.getChildren()) {
            if (node instanceof Button contentButton) {
                if (contentButton.getText().equals(content.getTitle())) {
                    contentPane.getChildren().remove(contentButton);
                    break;
                }
            }
        }
    }

    public void updateClicked() {
        try {
            // Make sure we have the current content selected and that it's not empty
            String updatedContent = contentArea.getText();
            String updatedTitle = titleField.getText();

            if (updatedContent.isEmpty() || updatedTitle.isEmpty()) {
                System.out.println("Please fill in the content and title.");
                return;
            }

            // Get the current section content ID from the currently selected content
            SectionContent content = currentSectionContent;

            if (content == null) {
                System.out.println("No content selected to update.");
                return;
            }

            // Create a new SectionContent record with updated information
            SectionContent updatedContentRecord = new SectionContent(
                    content.id(), // Use the existing ID for the updated record
                    content.sectionId(), // Section ID remains the same
                    updatedTitle,  // Updated title
                    content.contentType(),  // Assume content type doesn't change, if needed you can change it
                    updatedContent,  // New content
                    content.contentOrder() // You may want to keep the order the same, or update it
            );

            // Update the section content in the database
            SectionContentDao.updateSectionContent(updatedContentRecord);

            // Optionally, clear the input fields after updating
            titleField.clear();
            contentArea.clear();

            // Optionally, refresh the displayed content to reflect the update
            displaySectionContents(content.sectionId());

            System.out.println("Content updated successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }

    public void deleteSection() {
        try {
            if (currentSection == null) {
                System.out.println("No section selected to delete.");
                return;
            }

            // Delete the section from the database
            SectionDao.deleteSection(currentSection.getId());

            // Optionally, refresh the UI after deletion (to display all sections again)
            displaySectionsForCourse(currentCourse);
            contentScroll.setVisible(false);

            System.out.println("Section deleted successfully.");

            // Optionally, reset currentSection to null after deleting it
            currentSection = null;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }


    public void updateButton() {
        // Get the new title and description from the input fields
        String newTitle = titleField.getText();
        String newDescription = contentArea.getText();

        if (newTitle.isEmpty() || newDescription.isEmpty()) {
            System.out.println("Please provide new values for the section.");
            return;
        }

        contentScroll.setVisible(false);

        // Update the section in the database and UI
        if (currentSection != null) {
            try {
                // Update section in the database
                SectionDao.updateSection(currentSection.getId(), newTitle, newDescription);

                // Update the currentSection object to reflect the new values
                currentSection.setTitle(newTitle);
                currentSection.setDescription(newDescription);

                // Optionally, update the section button text to reflect the new title
                currentSectionButton.setText(newTitle);

                // Clear the input fields after updating
                titleField.clear();
                contentArea.clear();

                System.out.println("Section updated successfully.");
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "An error occurred", ex);
            }
        } else {
            System.out.println("No section selected for update.");
        }
    }

    public void deleteCourseClicked() {
        coursePane.setVisible(false);
        scrollPane.setVisible(true);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(false);
        quizzesScroll.setVisible(false);

        List<Course> courses = CourseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseButtonDelete(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();
    }

    public void makeQuizClicked()  {
        coursePane.setVisible(false);
        scrollPane.setVisible(true);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(false);
        quizzesScroll.setVisible(false);

        List<Course> courses = CourseDao.getCoursesByTutorId(tutor.getId()); // Get courses by tutor ID

        // Clear existing content in the AnchorPane before adding new courses
        vboxCourses.getChildren().clear(); // or AnchorPane, depending on your setup

        // Loop through each course and create a button for it
        for (int i = 0; i < courses.size(); i++) {
            Button courseButton = getCourseQuizButton(courses, i);

            // Add the button to the AnchorPane
            vboxCourses.getChildren().add(courseButton); // Replace with your AnchorPane variable
        }

        // Refresh the layout of the AnchorPane
        vboxCourses.layout();

    }

    @NotNull
    private Button getCourseQuizButton(List<Course> courses, int i) {
        double buttonWidth = 500;
        double buttonHeight = 100;

        Course course = courses.get(i);
        Button courseButton = new Button(course.getName()); // Create a button with the course name
        courseButton.setFont(new Font(20));
        courseButton.setPrefSize(buttonWidth, buttonHeight);
        courseButton.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-font-weight: bold;");
        courseButton.setAlignment(Pos.BASELINE_LEFT);
        courseButton.setWrapText(true);

        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/next.png"))));
        arrow.setFitHeight(32);
        arrow.setFitWidth(32);
        arrow.setPreserveRatio(true);
        courseButton.setGraphic(arrow);
        courseButton.setGraphicTextGap(10);
        courseButton.setOnMouseEntered(_ -> {
            courseButton.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.HAND);
        });

        courseButton.setOnMouseExited(_ -> {
            courseButton.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color:  lightgray;-fx-font-weight: bold;");
            courseButton.setCursor(Cursor.DEFAULT);
        });

        // Manually position the button inside AnchorPane
        courseButton.setLayoutX(20); // Set horizontal position
        courseButton.setLayoutY(i * 110 + 20); // Set vertical position with spacing between buttons

        courseButton.setOnAction(_ -> {
            // Clear previous section buttons and content before displaying new ones
            FXMLLoader loader;
            Parent nextPage;
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            Scene nextScene;
            loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/quiz-view.fxml"));

            try {
                nextPage = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            QuizViewController quizViewController= loader.getController();
            quizViewController.setCourse(course);
            quizViewController.setTutor(tutor);
            nextScene = new Scene(nextPage);
            // Set the stage to the previous size and position
            stage.setWidth(1600);
            stage.setHeight(1000);
            stage.centerOnScreen();

            // Set the new scene and show the stage
            stage.setScene(nextScene);
            stage.show();
        });
        return courseButton;
    }


    public void ViewQuizzes() {
        coursePane.setVisible(false);
        scrollPane.setVisible(false);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(true);
        quizzesScroll.setVisible(false);
        loadQuizzes();
    }

    public void deleteQuizClicked() {
        coursePane.setVisible(false);
        scrollPane.setVisible(false);
        sectionScroll.setVisible(false);
        contentScroll.setVisible(false);
        viewScrollPane.setVisible(false);
        quizzesScrollPane.setVisible(true);
        quizzesScroll.setVisible(false);
        loadQuizzesForDelete();
    }

    private void loadQuizzesForDelete() {
        QuizDao quizDao = new QuizDao();
        List<Quiz> quizzes = quizDao.getQuizzesByTutor(tutor.getId());  // Get quizzes for the tutor

        quizListVBox.getChildren().clear();
        // When the button is clicked, show the quiz
        quizListVBox.setAlignment(Pos.CENTER);      // Center children horizontally
        quizListVBox.setSpacing(20);
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
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Are you sure you want to delete this quiz?");
                alert.setContentText("Quiz: " + quiz.getTitle());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        // Delete from database
                        quizDao.deleteQuizById(quiz.getId());

                        // Remove button from VBox
                        quizListVBox.getChildren().remove(quizButton);
                    } catch (SQLException ex) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to delete quiz");
                        errorAlert.setContentText(ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });

            quizListVBox.getChildren().add(quizButton);
        }
    }

    public void changePasswordClicked() {
        passwordPane.setVisible(true);
        accountPane.setVisible(false);
    }

    public void changeEmailClicked() {
        emailPane.setVisible(true);
        accountPane.setVisible(false);
    }

    public void updatePassword() {
        try {
            TutorDao.updatePassword(tutor, newPasswordField.getText(), oldPasswordField.getText());
            passwordPane.setVisible(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEmail() {
        try {
            TutorDao.updateEmail(tutor, emailField.getText());
            emailPane.setVisible(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.example.gbsproject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePageViewController {
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
            stage.setMinWidth(1460);
            stage.setMinHeight(790);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-page-view.fxml"));
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
    protected  void homeButtonClick(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-page-view.fxml"));
            Parent root = loader.load();


            // Get the current stage (window)
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
        // Create the label for "Courses"
        Label coursesLabel = new Label("COURSES");
        coursesLabel.setFont(new Font("Arial", 30)); // Set large font size
        coursesLabel.setStyle("-fx-font-weight: bold;"); // Make the font bold
        coursesLabel.setAlignment(Pos.CENTER); // Center align the label text

        Label AITutorLabel = new Label("AI TUTOR");
        AITutorLabel.setFont(new Font("Arial", 30)); // Set large font size
        AITutorLabel.setStyle("-fx-font-weight: bold;"); // Make the font bold
        AITutorLabel.setAlignment(Pos.CENTER); // Center align the label text

        ImageView courses = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/online-course.png"))));
        courses.setFitHeight(40);
        courses.setFitWidth(40);
        courses.setPreserveRatio(true);

        ImageView aiTutor = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/generative.png"))));
        aiTutor.setFitHeight(45);
        aiTutor.setFitWidth(45);
        aiTutor.setPreserveRatio(true);

        // Position the label at the top of the scroll pane
        AnchorPane.setTopAnchor(coursesLabel, 250.0); // Adjust vertical positioning
        AnchorPane.setLeftAnchor(coursesLabel, 250.0); // Adjust horizontal positioning

        AnchorPane.setTopAnchor(courses, 250.0); // Adjust vertical positioning
        AnchorPane.setLeftAnchor(courses, 200.0); // Adjust horizontal positioning


        // Create the new AnchorPane for the AI Tutor section
        AnchorPane aiTutorSection = new AnchorPane();
        aiTutorSection.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 10;");

        // Create the title label
        Label aiTutorTitleLabel = new Label("Welcome to the AI Tutor");
        aiTutorTitleLabel.setFont(new Font("Arial", 24)); // Large title font
        aiTutorTitleLabel.setStyle("-fx-font-weight: bold;");

        Label aiTutorDescriptionLabel = getAiTutorDescriptionLabel();

        // Create the green button
        Button beginWithAITutorButton = new Button("Begin with AI Tutor");
        beginWithAITutorButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        beginWithAITutorButton.setFont(new Font("Arial", 18));
        beginWithAITutorButton.setPrefSize(200, 50);
        beginWithAITutorButton.setAlignment(Pos.CENTER);

        // Position the elements within the AI Tutor section
        AnchorPane.setTopAnchor(aiTutorTitleLabel, 20.0);
        AnchorPane.setLeftAnchor(aiTutorTitleLabel, 20.0);

        AnchorPane.setTopAnchor(aiTutorDescriptionLabel, 60.0);
        AnchorPane.setLeftAnchor(aiTutorDescriptionLabel, 20.0);

        AnchorPane.setTopAnchor(beginWithAITutorButton, 140.0);
        AnchorPane.setLeftAnchor(beginWithAITutorButton, 20.0);

        // Add the title, description, and button to the AI Tutor section
        aiTutorSection.getChildren().addAll(aiTutorTitleLabel, aiTutorDescriptionLabel, beginWithAITutorButton);

        // Bind the width of aiTutorSection to the mainAnchorPane width (dynamic resizing)
        aiTutorSection.prefWidthProperty().bind(mainAnchorPane.widthProperty().subtract(400));  // Subtract margins

        FlowPane buttonContainer = new FlowPane();
        buttonContainer.setHgap(10);
        buttonContainer.setVgap(10);
        buttonContainer.setPrefWrapLength(800); // Adjust width to allow wrapping
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-background-color: transparent;");

        double buttonWidth = 291;
        double buttonHeight = 100;

        int i;
        for (i = 1; i <= 8; i++) {
            Button button = new Button("Course " + i);
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
                button.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray; -fx-underline: true;-fx-font-weight: bold;");
                button.setCursor(Cursor.HAND);
            });

            button.setOnMouseExited(_ -> {
                button.setStyle("-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: lightgray;-fx-font-weight: bold;");
                button.setCursor(Cursor.DEFAULT);
            });

            buttonContainer.getChildren().add(button);
        }

        double topAnchor = 300.0;
        double flowPaneHeight = buttonHeight*(i-1)/3;
        double lastButtonY = topAnchor+flowPaneHeight;

        AnchorPane.setTopAnchor(buttonContainer, 300.0);
        AnchorPane.setLeftAnchor(buttonContainer, 150.0);

        AnchorPane.setTopAnchor(AITutorLabel, lastButtonY + 50); // Adjust vertical positioning
        AnchorPane.setLeftAnchor(AITutorLabel, 235.0); // Adjust horizontal positioning

        AnchorPane.setTopAnchor(aiTutor, lastButtonY + 50); // Adjust vertical positioning
        AnchorPane.setLeftAnchor(aiTutor, 185.0); // Adjust horizontal positioning

        // Set the vertical position of the AI Tutor section
        AnchorPane.setTopAnchor(aiTutorSection, lastButtonY + 100); // Adjust vertical position
        AnchorPane.setLeftAnchor(aiTutorSection, 150.0);

        // Add listener to resize width of ScrollPane based on window size
        mainAnchorPane.widthProperty().addListener((_, _, _) -> {
            // Adjust width of ScrollPane dynamically as the form resizes
            buttonContainer.prefWidthProperty().bind(mainAnchorPane.widthProperty().subtract(400));
        });

        mainAnchorPane.getChildren().add(courses);
        mainAnchorPane.getChildren().add(coursesLabel);
        mainAnchorPane.getChildren().add(buttonContainer);
        mainAnchorPane.getChildren().add(AITutorLabel);
        mainAnchorPane.getChildren().add(aiTutor);
        mainAnchorPane.getChildren().add(aiTutorSection);


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

}

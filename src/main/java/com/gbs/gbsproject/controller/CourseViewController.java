package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.SectionContentDao;
import com.gbs.gbsproject.dao.SectionDao;
import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.*;
import com.gbs.gbsproject.model.Section;
import com.gbs.gbsproject.service.CertificateService;
import com.gbs.gbsproject.service.TTSService;
import javafx.animation.PauseTransition;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CourseViewController {
    public AnchorPane passwordPane;
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public AnchorPane emailPane;
    public TextField emailField;
    Student student;
    private static final Logger LOGGER = Logger.getLogger(CourseViewController.class.getName());
    public Button buttonMenuStudies;
    public Button buttonMenuAccount;
    public AnchorPane accountPane;
    public AnchorPane helpPane;
    public AnchorPane studiesPane;
    public AnchorPane contentPane;
    public VBox sectionsVBox;
    public VBox contentVBox;
    public ScrollPane scrollPane;
    public AnchorPane mainAnchorPane;
    Course course;

    @FXML
    protected void formClicked() {
        accountPane.setVisible(false);
        helpPane.setVisible(false);
        studiesPane.setVisible(false);
        passwordPane.setVisible(false);
        emailPane.setVisible(false);
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

    public void setStudent(Student student) {
        this.student = student;
    }

    @NotNull
    private static TextFlow getTextFlow(Course course) {
        Text descriptionText = new Text(course.getDescription());  // Use the course description
        descriptionText.setStyle("-fx-font-size: 20px;"); // Set font size for the description

        // Create a TextFlow to wrap the description text
        TextFlow descriptionFlow = new TextFlow(descriptionText);
        descriptionFlow.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-border-color: lightgray;");
        descriptionFlow.setMaxWidth(1000);  // Ensure it wraps properly within the max width
        return descriptionFlow;
    }

    public void setCourse(Course course){
        this.course = course;
        Label titleLabel = new Label(course.getName()); // Set the course title
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;"); // Larger font size for the title
        titleLabel.setWrapText(true);  // Allow the title to wrap if it's too long
        titleLabel.setMaxWidth(1000);   // Set maximum width for the title

        // Create a TextFlow for the description with text styled at 20px
        TextFlow descriptionFlow = getTextFlow(course);

        // Add the title label and description flow to the VBox
        contentVBox.getChildren().clear();
        contentVBox.setSpacing(30);
        contentVBox.getChildren().addAll(titleLabel, descriptionFlow);

        loadSections();
    }

    private void loadSections() {
        try {
            List<Section> sections = SectionDao.getSectionsByCourseId(course.getId());

            for (Section section : sections) {
                Button sectionButton = new Button(section.getTitle());
                sectionButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 20px;" +
                                "-fx-border-color : white"
                );
                sectionButton.setAlignment(Pos.BASELINE_LEFT);
                sectionButton.setWrapText(true);

                // Add click event if needed
                sectionButton.setOnAction(_ ->
                {
                    displaySectionContents(section, contentVBox);
                    scrollPane.setVvalue(0); // Set vertical position to the top (0)
                });

                sectionsVBox.getChildren().add(sectionButton);
                sectionsVBox.setFillWidth(true);
                contentPane.setPrefHeight(3000);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }

    private void displaySectionContents(Section section, VBox contentVBox) {
        contentVBox.getChildren().clear();
        contentVBox.setSpacing(30);
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
            titleLabel.setMaxWidth(1000);

            contentVBox.getChildren().add(titleLabel);
            switch (content.getContentType()) {
                case "text" -> {
                    Button speechButton = getSpeechButton(content);

                    Button stopButton = new Button("");
                    stopButton.setStyle("-fx-font-size: 18px; -fx-background-color: red;");
                    stopButton.setCursor(Cursor.HAND);
                    ImageView stopIcon = new ImageView(new Image("/stop_icon.png"));
                    stopIcon.setFitWidth(15);
                    stopIcon.setFitHeight(15);

                    stopButton.setGraphic(stopIcon);
                    stopButton.setOnAction(_ -> TTSService.stopAudio());

                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.getChildren().add(speechButton);
                    hBox.getChildren().add(stopButton);

                    contentVBox.getChildren().add(hBox);

                    // Display the text content
                    Text text = new Text(content.getContent());
                    text.setStyle("-fx-font-size: 20px;");
                    text.setWrappingWidth(1000);

                    TextFlow textFlow = new TextFlow(text);
                    textFlow.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10px; -fx-border-color: lightgray;");
                    textFlow.setMaxWidth(1000);  // Ensure it wraps properly within the max width
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
                        javafx.scene.image.Image image = new Image(content.getContent(), true); // 'true' loads in background
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(1000); // Adjust as needed
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
    }

    @NotNull
    private static Button getSpeechButton(SectionContent content) {
        Button speechButton = new Button("");
        speechButton.setStyle("-fx-font-size: 18px; -fx-background-color: #28a745;");
        speechButton.setCursor(Cursor.HAND);

        ImageView playIcon = new ImageView(new Image("/play_icon.png"));
        playIcon.setFitWidth(15);
        playIcon.setFitHeight(15);

        speechButton.setGraphic(playIcon);

        speechButton.setOnAction(_ -> {
            TTSService.generateSpeech(content.getContent());
            if(!TTSService.getIsPlaying()){
                speechButton.setText("Please wait...");

                PauseTransition pause = new PauseTransition(Duration.seconds(7));
                pause.setOnFinished(_ -> speechButton.setText(""));
                pause.play();
            }
        });
        return speechButton;
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

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("There was an issue opening the next page.");
        alert.showAndWait();
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
        }
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
}

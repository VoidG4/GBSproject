package com.gbs.gbsproject.controller;

import com.itextpdf.text.Rectangle;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
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
            // Load the FXML file for the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/gpa-view.fxml"));
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
    protected void certificateClicked(){
        try {
            // Create and save the PDF in the Downloads folder
            String pdfPath = savePdfToDownloads();

            String os = System.getProperty("os.name").toLowerCase();  // Get the OS name and convert to lowercase

            try {
                ProcessBuilder processBuilder;

                if (os.contains("win")) {
                    // Command to open Chrome (Windows example)
                    processBuilder = new ProcessBuilder("cmd", "/c", "start", "chrome", pdfPath);

                } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                    // Command for Linux or macOS (xdg-open on Linux, open on macOS)
                    if (os.contains("mac")) {
                        processBuilder = new ProcessBuilder("open", pdfPath);  // macOS-specific
                    } else {
                        processBuilder = new ProcessBuilder("xdg-open", pdfPath);  // Linux-specific
                    }

                } else {
                    System.out.println("Unknown OS: " + os);
                    return;
                }

                // Start the process
                Process process = processBuilder.start();
                process.waitFor();  // Optionally wait for the process to finish

            } catch (IOException | InterruptedException e) {
                System.out.println("Error opening URL or file: " + e.getMessage());
            }

        } catch (Exception e) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/home-page-view.fxml"));
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
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(1600);
        scrollPane.setPrefHeight(780);
        scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent;");

        // Stops the event from giving focus to the ScrollPane
        scrollPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, Event::consume);


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

        Label announcementLabel = new Label("ANNOUNCEMENTS");
        announcementLabel.setFont(new Font("Arial", 30));
        announcementLabel.setStyle("-fx-font-weight: bold;-fx-background-color: transparent; -fx-text-fill: black;");
        announcementLabel.setAlignment(Pos.CENTER);

        ImageView courses = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/online-course.png"))));
        courses.setFitHeight(40);
        courses.setFitWidth(40);
        courses.setPreserveRatio(true);

        ImageView aiTutor = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/generative.png"))));
        aiTutor.setFitHeight(45);
        aiTutor.setFitWidth(45);
        aiTutor.setPreserveRatio(true);

        ImageView announcements = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/megaphone.png"))));
        announcements.setFitHeight(45);
        announcements.setFitWidth(45);
        announcements.setPreserveRatio(true);

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

        Button beginWithAITutorButton = new Button("Begin with AI Tutor");
        beginWithAITutorButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        beginWithAITutorButton.setFont(new Font("Arial", 18));
        beginWithAITutorButton.setPrefSize(200, 50);
        beginWithAITutorButton.setAlignment(Pos.CENTER);

        AnchorPane.setTopAnchor(aiTutorTitleLabel, 20.0);
        AnchorPane.setLeftAnchor(aiTutorTitleLabel, 20.0);

        AnchorPane.setTopAnchor(aiTutorDescriptionLabel, 60.0);
        AnchorPane.setLeftAnchor(aiTutorDescriptionLabel, 20.0);

        AnchorPane.setTopAnchor(beginWithAITutorButton, 140.0);
        AnchorPane.setLeftAnchor(beginWithAITutorButton, 20.0);

        aiTutorSection.getChildren().addAll(aiTutorTitleLabel, aiTutorDescriptionLabel, beginWithAITutorButton);
        aiTutorSection.prefWidthProperty().bind(contentPane.widthProperty().subtract(400));


        AnchorPane announcementPane = new AnchorPane();
        announcementPane.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 10;");

        Label titleLabel = new Label("News");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: black;");

        Label descriptionLabel = getAnnouncementDescriptionLabel();

        Button actionButton = new Button("Learn More");
        actionButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        actionButton.setFont(new Font("Arial", 18));
        actionButton.setPrefSize(200, 50);
        actionButton.setAlignment(Pos.CENTER);

        AnchorPane.setTopAnchor(titleLabel, 20.0);
        AnchorPane.setLeftAnchor(titleLabel, 20.0);

        AnchorPane.setTopAnchor(descriptionLabel, 60.0);
        AnchorPane.setLeftAnchor(descriptionLabel, 20.0);

        AnchorPane.setTopAnchor(actionButton, 140.0);
        AnchorPane.setLeftAnchor(actionButton, 20.0);
        announcementPane.getChildren().addAll(titleLabel, descriptionLabel, actionButton);
        announcementPane.prefWidthProperty().bind(contentPane.widthProperty().subtract(400));



        FlowPane buttonContainer = new FlowPane(10, 10);
        buttonContainer.setPrefWrapLength(1000);
        buttonContainer.setAlignment(Pos.TOP_LEFT);
        buttonContainer.setStyle("-fx-background-color: transparent;");

        double buttonWidth = 385;
        double buttonHeight = 100;

        int i;
        for (i = 1; i <= 6 ; i++) {
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
                button.setStyle("-fx-border-width: 3px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color: #72B3FF; -fx-underline: true;-fx-font-weight: bold;");
                button.setCursor(Cursor.HAND);
            });

            button.setOnMouseExited(_ -> {
                button.setStyle("-fx-border-width: 1px;-fx-background-color: white; -fx-text-fill: #4682B4; -fx-border-color:  lightgray;-fx-font-weight: bold;");
                button.setCursor(Cursor.DEFAULT);
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

        AnchorPane.setTopAnchor(announcementLabel, topAnchor + buttonContainerHeight + 450);
        AnchorPane.setLeftAnchor(announcementLabel, 235.0);

        AnchorPane.setTopAnchor(announcements, topAnchor + buttonContainerHeight+ 450);
        AnchorPane.setLeftAnchor(announcements, 185.0);

        AnchorPane.setTopAnchor(announcementPane, topAnchor + buttonContainerHeight+ 500);
        AnchorPane.setLeftAnchor(announcementPane, 150.0);

        contentPane.getChildren().addAll(courses, coursesLabel, buttonContainer, AITutorLabel, aiTutor, aiTutorSection, announcementLabel, announcements, announcementPane);
        scrollPane.setContent(contentPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure vertical scroll bar

        mainAnchorPane.getChildren().add(scrollPane);
    }

    // Method to create PDF and save it in the Downloads folder
    private String savePdfToDownloads() {
        try {
            // Define the path where the PDF will be saved
            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + File.separator + "Downloads";
            String pdfPath = downloadsPath + File.separator + "Certificate_of_Completion.pdf";

            // Create the document in landscape orientation
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            // Open the document for writing
            document.open();

            // Load the background image
            String backgroundPath = "/home/gat/IdeaProjects/GBSproject/src/main/resources/background.png";
            com.itextpdf.text.Image backgroundImage = com.itextpdf.text.Image.getInstance(backgroundPath);
            backgroundImage.setAbsolutePosition(0, 90);
            backgroundImage.scaleToFit(PageSize.A4.rotate().getWidth(), PageSize.A4.rotate().getHeight()); // Scale it to cover the whole page
            document.add(backgroundImage);

            // Add "Congratulations" heading in black font
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 48, com.itextpdf.text.Font.BOLD, BaseColor.BLACK); // Black color
            Paragraph title = new Paragraph("Congratulations!", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(100); // Add space before title
            document.add(title);

            // Add certificate body text in black font
            com.itextpdf.text.Font bodyFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 24, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK); // Black color
            Paragraph body = new Paragraph("This certificate is awarded to", bodyFont);
            body.setAlignment(Element.ALIGN_CENTER);
            body.setSpacingAfter(20);
            document.add(body);

            // Add recipient name placeholder (you can change this dynamically later)
            com.itextpdf.text.Font nameFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 30, com.itextpdf.text.Font.BOLDITALIC, BaseColor.BLACK); // Black color
            Paragraph recipient = new Paragraph("[Recipient Name]", nameFont);
            recipient.setAlignment(Element.ALIGN_CENTER);
            recipient.setSpacingAfter(40);
            document.add(recipient);

            // Add the certificate details
            Paragraph details = new Paragraph("For successfully completing the course at\n" +
                    "Greece Business School", bodyFont);
            details.setAlignment(Element.ALIGN_CENTER);
            details.setSpacingAfter(40);
            document.add(details);

            // Add a line (for separation)
            Paragraph line = new Paragraph("------------------------------------------------------------");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(40);
            document.add(line);


            // Add the blue rectangle at the bottom
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle blueRectangle = new Rectangle(0, 0, PageSize.A4.rotate().getWidth(), 100); // Rectangle at the bottom
            BaseColor lightBlue = new BaseColor(40, 126, 255);
            blueRectangle.setBackgroundColor(lightBlue); // Blue color
            canvas.rectangle(blueRectangle);
            canvas.fill(); // Fill the rectangle

            // Add the date
            Paragraph date = new Paragraph("Date: April 2025", bodyFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            // Close the document
            document.close();

            return pdfPath;

        } catch (Exception e) {
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the next FXML", e);
            // Optionally, show a dialog to notify the user of the error
            showErrorDialog();
        }
        return null;
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
    private static Label getAnnouncementDescriptionLabel() {
        Label descriptionLabel = new Label("Stay updated with the latest announcements and important updates here. Never miss out on crucial information!");
        descriptionLabel.setFont(new Font("Arial", 18));
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(600);
        descriptionLabel.setStyle("-fx-text-fill: #333;");
        return descriptionLabel;
    }
}
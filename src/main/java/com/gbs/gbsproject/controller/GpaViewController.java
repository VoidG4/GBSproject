package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.Certificate;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.service.CertificateService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.gbs.gbsproject.dao.CourseDao.getAllCourses;

public class GpaViewController {
    public ScrollPane scrollPane;
    public AnchorPane mainAnchorPane;
    public AnchorPane passwordPane;
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public AnchorPane emailPane;
    public TextField emailField;
    public VBox mainVBox;
    public Circle gpaProgressCircle;
    Student student;

    private static final Logger LOGGER = Logger.getLogger(GpaViewController.class.getName());
    public AnchorPane studiesPane;
    public AnchorPane helpPane;
    public AnchorPane accountPane;
    public Button buttonMenuStudies;
    public Button buttonMenuAccount;
    public Circle gpaBackgroundCircle;
    public VBox courseContainer;
    @FXML private Label gpaLabel;

    @FXML
    public void initialize() {}

    private void updateGPAArc(double gpa) {
        double progress = gpa / 10.0; // GPA from 0.0 to 10.0
        double radius = 80;
        double circumference = 2 * Math.PI * radius;

        // Circle stroke draws centered, so we use dash array to simulate arc
        Circle circle = gpaProgressCircle; // fx:id
        circle.getStrokeDashArray().setAll(circumference, circumference);
        circle.setStrokeDashOffset(circumference * (1 - progress));

        gpaLabel.setText(String.format("%.1f", gpa));
    }


    public void loadStudentData() {
        List<Course> courseList = getAllCourses();

        double sum = 0;
        for (Course course : courseList) {
            VBox courseBox = new VBox(5);
            courseBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
            courseBox.setFillWidth(true);

            double grade = StudentDao.getGrade(course.getId(), student.getId());
            sum += grade;
            Label nameLabel = new Label(course.getName() + ": " + grade);
            nameLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 18px;");

            ProgressBar progressBar = new ProgressBar(grade/10);
            progressBar.setVisible(true);

            String color;
            if (grade <= 4) {
                color = "#FF4C4C"; // Red
            } else if (grade <= 7) {
                color = "#FFD700"; // Yellow
            } else {
                color = "#00C9A7"; // Green/Teal
            }
            progressBar.setStyle("-fx-accent: " + color + "; -fx-pref-width: 200px;");

            courseBox.getChildren().add(nameLabel);
            courseBox.getChildren().add(progressBar);
            courseContainer.getChildren().add(courseBox);
            scrollPane.setContent(courseContainer);
            courseContainer.setPrefWidth(500); // Set to your desired width in pixels
            mainVBox.setPrefWidth(550);
            mainVBox.setPrefHeight(700);
            scrollPane.setPrefHeight(700);

            AnchorPane.setLeftAnchor(mainVBox, 490.0);
        }

        int number0fCourses = courseList.size();

        double gpa = sum / number0fCourses;

        updateGPAArc(gpa);
    }

    public void setStudent(Student student){
        this.student = student;
        loadStudentData();
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

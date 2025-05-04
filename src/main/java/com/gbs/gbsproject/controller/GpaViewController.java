package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.Student;
import com.itextpdf.text.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
    Student student;

    private static final Logger LOGGER = Logger.getLogger(GpaViewController.class.getName());
    public AnchorPane studiesPane;
    public AnchorPane helpPane;
    public AnchorPane accountPane;
    public Button buttonMenuStudies;
    public Button buttonMenuAccount;
    public Circle gpaBackgroundCircle;
    public VBox courseContainer;
    @FXML private Arc gpaArc;
    @FXML private Label gpaLabel;

    @FXML
    public void initialize() {}

    public void loadStudentData() {
        List<Course> courseList = getAllCourses();

        double sum = 0;
        for (Course course : courseList) {
            VBox courseBox = new VBox(5);
            courseBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
            courseBox.setFillWidth(true);

            Label nameLabel = new Label(course.getName());
            nameLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

            double grade = StudentDao.getGrade(course.getId(), student.getId());
            sum += grade;

            ProgressBar progressBar = new ProgressBar(0.5);
            progressBar.setVisible(true);
            progressBar.setStyle("-fx-accent: #00C9A7;");

            courseBox.getChildren().add(nameLabel);
            courseBox.getChildren().add(progressBar);
            courseContainer.getChildren().add(courseBox);
            scrollPane.setContent(courseContainer);
        }

        int number0fCourses = courseList.size();

        double gpa = sum / number0fCourses;
        double angle = gpa * 36.0; // 10 GPA → 360 degrees

        gpaArc.setStartAngle(90);     // Start at the bottom
        gpaArc.setLength(-angle);     // NEGATIVE makes it draw clockwise!
        gpaLabel.setText(String.format("%.1f", gpa));
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

    private String savePdfToDownloads() {
        try {
            // Define the path where the PDF will be saved
            String userHome = System.getProperty("user.home");
            String downloadsPath = userHome + File.separator + "Downloads";
            String pdfPath = downloadsPath + File.separator + "Certificate_of_Completion.pdf";

            // Create the document in landscape orientation
            Document document = new Document(PageSize.A4.rotate());
            //PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            // Open the document for writing
            document.open();

            // Load the background image
            String backgroundPath = "/home/gat/IdeaProjects/GBSproject/src/main/resources/background.jpg";
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

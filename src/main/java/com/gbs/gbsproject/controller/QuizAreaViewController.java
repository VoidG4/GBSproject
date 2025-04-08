package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.CourseDao;
import com.gbs.gbsproject.dao.QuizDao;
import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.model.Question;
import com.gbs.gbsproject.model.Quiz;
import com.itextpdf.text.*;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuizAreaViewController {
    private static final Logger LOGGER = Logger.getLogger(QuizAreaViewController.class.getName());

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
        Button submitBtn = getSubmitBtn(questions, userAnswers);


        containerVBox.getChildren().add(submitBtn);
    }

    @NotNull
    private static Button getSubmitBtn(List<Question> questions, Map<Integer, String> userAnswers) {
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
}

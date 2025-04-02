package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.service.GeminiService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiController {
    private static final Logger LOGGER = Logger.getLogger(GeminiController.class.getName());
    public Button buttonMenuStudies;
    public javafx.scene.control.Button buttonMenuAccount;
    public AnchorPane accountPane;
    public AnchorPane helpPane;
    public AnchorPane studiesPane;
    public AnchorPane mainAnchorPane;
    public TextField userInputField;
    private VBox chatBox;
    private ScrollPane scrollPane;
    @FXML
    private ImageView loading;
    @FXML
    private Button buttonAsk;

    @FXML
    private void initialize(){
        Label AITutorLabel = new Label("AI TUTOR");
        AITutorLabel.setFont(new Font("Arial", 30));
        AITutorLabel.setStyle("-fx-font-weight: bold;-fx-background-color: transparent;-fx-text-fill: black;");
        AITutorLabel.setAlignment(Pos.CENTER);

        ImageView aiTutor = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/generative.png"))));
        aiTutor.setFitHeight(45);
        aiTutor.setFitWidth(45);
        aiTutor.setPreserveRatio(true);

        AnchorPane.setTopAnchor(AITutorLabel,  240.0);
        AnchorPane.setLeftAnchor(AITutorLabel, 380.0);

        AnchorPane.setTopAnchor(aiTutor, 240.0);
        AnchorPane.setLeftAnchor(aiTutor, 320.0);

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setPrefWidth(1600);
        scrollPane.setPrefHeight(780);
        scrollPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.05); -fx-background: transparent ;-fx-background-radius: 50px;-fx-vbar-policy: never; -fx-hbar-policy: never;");

        chatBox = new VBox(10);
        chatBox.setStyle("-fx-background-color: transparent;");
        scrollPane.setContent(chatBox);

        mainAnchorPane.getChildren().add(AITutorLabel);  // Add the label
        mainAnchorPane.getChildren().add(aiTutor);       // Add the image
        mainAnchorPane.getChildren().add(scrollPane);

        mainAnchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                // Perform the action when Enter is pressed
                onButtonAskClicked();
            }
        });

        buttonAsk.setOnAction(_ -> onButtonAskClicked());

        AnchorPane.setTopAnchor(scrollPane, 280.0);
        AnchorPane.setLeftAnchor(scrollPane, 300.0);
        AnchorPane.setRightAnchor(scrollPane, 300.0);
        AnchorPane.setBottomAnchor(scrollPane, 130.0);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure vertical scroll bar

        Image i = new Image(new File("src/main/resources/loading.gif").toURI().toString());
        loading.setImage(i);
        loading.setFitHeight(50);
        loading.setFitWidth(60);
    }

    private void displayMessage(String message, boolean isUser) {
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-padding: 10px; -fx-background-radius: 10px;");
        Font customFont = Font.font("Droid Sans Mono Dotted", 26);
        textFlow.setMaxWidth(500);

        Pattern pattern = Pattern.compile("(\\*\\*([^*]+)\\*\\*)|(\\*([^*]+))");
        Matcher matcher = pattern.matcher(message);
        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                Text normalText = new Text(message.substring(lastIndex, matcher.start()));
                normalText.setFont(customFont); // Apply the custom font
                textFlow.getChildren().add(normalText);
            }
            if (matcher.group(1) != null) { // Bold text
                Text boldText = new Text(matcher.group(2));
                boldText.setFont(Font.font("Droid Sans Mono Dotted", javafx.scene.text.FontWeight.BOLD, 26)); // Set bold font
                textFlow.getChildren().add(boldText);
            } else if (matcher.group(3) != null) { // Bullet point
                Text bulletText = new Text("• " + matcher.group(4));
                bulletText.setFont(customFont); // Apply the custom font
                textFlow.getChildren().add(bulletText);
            }
            lastIndex = matcher.end();
        }
        if (lastIndex < message.length()) {
            Text remainingText = new Text(message.substring(lastIndex));
            remainingText.setFont(customFont); // Apply the custom font
            textFlow.getChildren().add(remainingText);
        }

        HBox messageContainer = new HBox(textFlow);
        if (isUser) {
            textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 10px; -fx-background-radius: 10px;");
            messageContainer.setStyle("-fx-alignment: center-right;");
        } else {
            textFlow.setStyle("-fx-background-color: #D6D6D6; -fx-padding: 10px; -fx-background-radius: 10px;");
            messageContainer.setStyle("-fx-alignment: center-left;");
        }

        chatBox.getChildren().add(messageContainer);
        scrollPane.setVvalue(1.0); // Auto-scroll to the bottom
        loading.setVisible(false);
    }

    @FXML
    protected void onButtonAskClicked(){
        String userMessage = userInputField.getText().trim();
        if (!userMessage.isEmpty()) {
            displayMessage(userMessage, true); // User message on the right
            userInputField.clear();
            new Thread(() -> {
                try {
                    loading.setVisible(true);
                    String response = GeminiService.askGemini(userMessage);
                    Platform.runLater(() -> displayMessage(response, false)); // Gemini's response on the left
                } catch (IOException e) {
                    Platform.runLater(() -> displayMessage("Error: Unable to get a response", false));
                }
            }).start();
        }
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
}

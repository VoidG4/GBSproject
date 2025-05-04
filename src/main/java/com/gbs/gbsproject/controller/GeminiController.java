package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.dao.GeminiDao;
import com.gbs.gbsproject.dao.StudentDao;
import com.gbs.gbsproject.model.Chat;
import com.gbs.gbsproject.model.Message;
import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.service.GeminiService;
import com.itextpdf.text.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.text.Font;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
    public AnchorPane passwordPane;
    public TextField oldPasswordField;
    public TextField newPasswordField;
    public AnchorPane emailPane;
    public TextField emailField;
    public Button microphone;
    private VBox chatBox;
    Student student;
    private ScrollPane scrollPane;
    @FXML
    private ImageView loading;
    @FXML
    private Button buttonAsk;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    private VBox chatHistoryBox;
    private int currentChatId = -1;

    public void setStudent(Student student) {
        this.student = student;
        loadChatHistory();
    }

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

        // VBox to hold chat history buttons
        chatHistoryBox = new VBox(10);
        chatHistoryBox.setPadding(new Insets(10));
        chatHistoryBox.setStyle("-fx-background-color: transparent; -fx-background-radius: 10;");
        chatHistoryBox.setPrefWidth(200);
        chatHistoryBox.setMinHeight(300);  // Minimum height so that it is visible and has space

        Button newChatButton = new Button("New Chat (+)");
        newChatButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
        newChatButton.setMaxWidth(Double.MAX_VALUE);
        newChatButton.setOnAction(_ -> onNewChatClicked());
        newChatButton.setMinHeight(20);  // Minimum height so that it is visible and has space


        mainAnchorPane.getChildren().add(newChatButton);
        AnchorPane.setTopAnchor(newChatButton, 250.0);
        AnchorPane.setRightAnchor(newChatButton, 80.0);


        // ScrollPane for chat history
        ScrollPane chatHistoryScrollPane = new ScrollPane();
        chatHistoryScrollPane.setFitToWidth(true);
        chatHistoryScrollPane.setPannable(true);
        chatHistoryScrollPane.setStyle("-fx-background: transparent; -fx-background-color: rgba(0, 0, 0, 0.05); -fx-background-radius: 10;");
        chatHistoryScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatHistoryScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatHistoryScrollPane.setPrefWidth(200);


        chatHistoryScrollPane.setContent(chatHistoryBox);

        mainAnchorPane.getChildren().add(chatHistoryScrollPane);

        AnchorPane.setTopAnchor(scrollPane, 280.0);
        AnchorPane.setLeftAnchor(scrollPane, 300.0);
        AnchorPane.setRightAnchor(scrollPane, 300.0);
        AnchorPane.setBottomAnchor(scrollPane, 130.0);

        AnchorPane.setTopAnchor(chatHistoryScrollPane, 280.0);
        AnchorPane.setRightAnchor(chatHistoryScrollPane, 80.0);
        AnchorPane.setBottomAnchor(chatHistoryScrollPane, 130.0);


        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Ensure vertical scroll bar

        Image i = new Image(new File("src/main/resources/loading.gif").toURI().toString());
        loading.setImage(i);
        loading.setFitHeight(60);
        loading.setFitWidth(70);

        ImageView micIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/microphone.png"))));
        micIcon.setFitWidth(45);
        micIcon.setFitHeight(45);
        microphone.setGraphic(micIcon);
        microphone.setCursor(Cursor.HAND);
    }

    private void onNewChatClicked() {
        try {
            // Set current chat ID
            currentChatId = GeminiDao.createNewChat(student.getId());

            // Clear the chat messages container
            chatBox.getChildren().clear();

            // Reload the chat history buttons (including the new one)
            loadChatHistory();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }


    private void loadChatHistory() {
        // Clear existing buttons in history box
        chatHistoryBox.getChildren().clear();

        // Create an instance of GeminiDao to fetch chat history
        GeminiDao geminiDao = new GeminiDao();

        // Retrieve the chat history for the user
        List<Chat> chats = geminiDao.getChatHistory(student.getId()); // Assuming userId is already set

        // Loop through the retrieved chats and create buttons for each one
        for (Chat chat : chats) {
            // Create a button for each chat
            Button chatButton = new Button(chat.title());
            chatButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: lightgray");
            chatButton.setCursor(Cursor.HAND);
            chatButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));

            chatButton.setMaxWidth(Double.MAX_VALUE);
            chatButton.setUserData(chat.id());
            chatButton.setOnMouseClicked(event -> {
                // Get the button that was clicked
                Button clickedButton = (Button) event.getSource();

                // Get the chat ID from user data
                currentChatId = (int) clickedButton.getUserData();

            });
            // Action when clicking on a chat button (load chat in main scroll pane)

            chatButton.setOnAction(_ -> loadChatMessages(chat.id()));

            Image image = new Image("/delete.png");
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(20);
            imageView.setFitHeight(22);

            // Create a delete button for each chat
            Button deleteButton = new Button();
            deleteButton.setCursor(Cursor.HAND);
            deleteButton.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-text-fill: white;");
            deleteButton.setGraphic(imageView);
            deleteButton.setOnAction(_ -> {
                // Delete the chat when the delete button is clicked
                geminiDao.deleteChat(chat.id());
                chatBox.getChildren().clear();
                // Get the chat ID from user data
                currentChatId = -1;
                loadChatHistory(); // Reload chat history after deletion
            });



            // Add the chat button and the delete button to the chat history box
            HBox chatHistoryItem = new HBox(10, chatButton, deleteButton);
            chatHistoryItem.setStyle("-fx-padding: 5;");
            chatHistoryBox.getChildren().add(chatHistoryItem);
        }
    }

    private void loadChatMessages(int chatId) {
        // Clear the existing chat messages
        chatBox.getChildren().clear();

        // Get the messages for the selected chat from the database
        GeminiDao geminiDao = new GeminiDao();
        List<Message> messages = geminiDao.getChatMessages(chatId);
        Font customFont = Font.font("Droid Sans Mono Dotted", 26);

        // Loop through the messages and add them to the chatbox as TextFlow
        for (Message message : messages) {
            TextFlow textFlow = new TextFlow();
            textFlow.setStyle("-fx-padding: 10px; -fx-background-radius: 10px;");
            textFlow.setMaxWidth(700);

            Pattern pattern = Pattern.compile("(\\*\\*([^*]+)\\*\\*)|(\\*([^*]+))");
            Matcher matcher = pattern.matcher(message.message());
            int lastIndex = 0;

            while (matcher.find()) {
                if (matcher.start() > lastIndex) {
                    Text normalText = new Text(message.message().substring(lastIndex, matcher.start()));
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
            if (lastIndex < message.message().length()) {
                Text remainingText = new Text(message.message().substring(lastIndex));
                remainingText.setFont(customFont); // Apply the custom font
                textFlow.getChildren().add(remainingText);
            }

            HBox messageContainer = new HBox(textFlow);
            if ("user".equals(message.sender())) {
                textFlow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 10px; -fx-background-radius: 10px;");
                messageContainer.setStyle("-fx-alignment: center-right;");
            } else if ("gemini".equals(message.sender())) {
                textFlow.setStyle("-fx-background-color: #D6D6D6; -fx-padding: 10px; -fx-background-radius: 10px;");
                messageContainer.setStyle("-fx-alignment: center-left;");
            }

            chatBox.getChildren().add(messageContainer);
            loading.setVisible(false);
        }
        // Automatically scroll to the bottom after loading new messages
        scrollPane.setVvalue(1.0);
    }


    private void displayMessage(String message, boolean isUser) {
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-padding: 10px; -fx-background-radius: 10px;");
        Font customFont = Font.font("Droid Sans Mono Dotted", 26);
        textFlow.setMaxWidth(700);

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


        String sender = isUser ? "user" : "gemini";
        try {
            GeminiDao.saveMessage(currentChatId, sender, message);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void button1MouseEntered(){
        button1.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-border-radius: 20px;-fx-text-fill: black;");
    }

    @FXML
    protected void button1MouseExited(){
        button1.setStyle("-fx-background-color: transparent;-fx-border-color: #3498db;-fx-border-radius: 20px;-fx-text-fill: #3498db;");
    }

    @FXML
    protected void button2MouseEntered(){
        button2.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-border-radius: 20px;-fx-text-fill: black;");
    }

    @FXML
    protected void button2MouseExited(){
        button2.setStyle("-fx-background-color: transparent;-fx-border-color: #3498db;-fx-border-radius: 20px;-fx-text-fill: #3498db;");
    }

    @FXML
    protected void button3MouseEntered(){
        button3.setStyle("-fx-background-color: rgba(0,0,0,0.05);-fx-border-radius: 20px;-fx-text-fill: black;");
    }

    @FXML
    protected void button3MouseExited(){
        button3.setStyle("-fx-background-color: transparent;-fx-border-color: #3498db;-fx-border-radius: 20px;-fx-text-fill: #3498db;");
    }

    @FXML
    protected void studyPlanClicked(){
        userInputField.setText("Make me a study plan about: ");
        userInputField.requestFocus();
        userInputField.positionCaret(userInputField.getText().length());
    }

    @FXML
    protected void summaryClicked(){
        userInputField.setText("Give me a summary on: ");
        userInputField.requestFocus();
        userInputField.positionCaret(userInputField.getText().length());
    }

    @FXML
    protected void quizClicked(){
        userInputField.setText("Make a quiz to test me on: ");
        userInputField.requestFocus();
        userInputField.positionCaret(userInputField.getText().length());
    }


    @FXML
    protected void onButtonAskClicked() {
        String userMessage = userInputField.getText().trim();

        if (currentChatId == -1) {
            // Create a new chat in the database
            int newChatId;
            try {
                newChatId = GeminiDao.createNewChat(student.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            currentChatId = newChatId;
            chatBox.getChildren().clear();
            loadChatHistory();

            // Ask Gemini for a title suggestion using the user's first message
            new Thread(() -> {
                try {
                    String titlePrompt = "Generate a short, two-word, based on this message: \"" + userMessage + "\"";
                    String suggestedTitle = GeminiService.askGemini(titlePrompt);

                    // Clean: remove anything that's not a letter or space
                    suggestedTitle = suggestedTitle.replaceAll("[^a-zA-Z ]", "").trim();

                    // Ensure exactly 2 words
                    String[] words = suggestedTitle.split("\\s+");
                    if (words.length >= 2) {
                        suggestedTitle = words[0] + " " + words[1];
                    } else {
                        suggestedTitle = "New Chat"; // Fallback
                    }

                    // Save to DB
                    GeminiDao.setChatTitle(currentChatId, suggestedTitle);

                    // Refresh UI
                    Platform.runLater(this::loadChatHistory);
                } catch (IOException | SQLException e) {
                    System.err.println("Failed to generate or save chat title: " + e.getMessage());
                }
            }).start();
        }

        if (!userMessage.isEmpty()) {
            displayMessage(userMessage, true); // User message on the right
            userInputField.clear();

            new Thread(() -> {
                try {
                    loading.setVisible(true);
                    String response = GeminiService.askGemini(userMessage);
                    Platform.runLater(() -> displayMessage(response, false));
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
        passwordPane.setVisible(false);
        emailPane.setVisible(false);
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

    // Method to create PDF and save it in the Downloads folder
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

    public void microphoneClicked() {
        FXMLLoader loader;
        Parent nextPage;
        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        Scene nextScene;

        loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/voice-view.fxml"));
        try {
            nextPage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VoiceViewController voiceViewController = loader.getController();
        voiceViewController.setStudent(student); // Pass Student object to the controller
        nextScene = new Scene(nextPage);
        // Set the stage to the previous size and position
        stage.setWidth(1600);
        stage.setHeight(1000);
        stage.centerOnScreen();

        // Set the new scene and show the stage
        stage.setScene(nextScene);
        stage.show();
    }
}

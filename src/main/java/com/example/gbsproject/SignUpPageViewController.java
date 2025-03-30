package com.example.gbsproject;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpPageViewController {
    private static final Logger LOGGER = Logger.getLogger(SignUpPageViewController.class.getName());

    public AnchorPane mainAnchorPane;
    public void initialize() {

        Platform.runLater(() -> {
            // Request focus on the mainAnchorPane to remove focus from text fields
            mainAnchorPane.requestFocus();
        });

        // Create the GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER); // Ensure it is centered horizontally
        gridPane.setStyle("-fx-background-color: white;"); // Very light gray background
        gridPane.setMinSize(450, 500);
        gridPane.setMaxSize(450, 500);

        // Create UI elements
        Font customFont = Font.font("Droid Sans Mono Dotted", 16);
        Font titleFont = Font.font("Droid Sans Mono Dotted", 25);

        Label lblTitle = new Label("Sign Up");
        Label lblText = new Label("Please fill in the required details");
        lblTitle.setFont(titleFont);
        lblText.setFont(customFont);

        TextField txtUsername = new TextField();
        txtUsername.setFont(customFont);
        txtUsername.setPromptText("Username");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setFont(customFont);
        txtPassword.setPromptText("Password");

        PasswordField txtPasswordConfirm = new PasswordField();
        txtPasswordConfirm.setFont(customFont);
        txtPasswordConfirm.setPromptText("Confirm Password");

        TextField txtName = new TextField();
        txtName.setFont(customFont);
        txtName.setPromptText("Name");

        TextField txtSurname = new TextField();
        txtSurname.setFont(customFont);
        txtSurname.setPromptText("Surname");

        TextField txtEmail= new TextField();
        txtEmail.setFont(customFont);
        txtEmail.setPromptText("Email");

        TextField txtPhone= new TextField();
        txtPhone.setFont(customFont);
        txtPhone.setPromptText("Phone");

        Button btnSignUp = new Button("Register");
        btnSignUp.setFont(customFont);
        btnSignUp.setStyle("-fx-background-color: rgba(2,93,11,0.8); -fx-text-fill: white;-fx-font-size: 16px; -fx-padding: 10px 15px;"); // Light green with white text

        txtUsername.setPrefWidth(300);
        txtPassword.setPrefWidth(300);
        txtPasswordConfirm.setPrefWidth(300);
        txtName.setPrefWidth(300);
        txtSurname.setPrefWidth(300);
        txtEmail.setPrefWidth(300);
        txtPhone.setPrefWidth(300);

        // Add elements to GridPane
        gridPane.add(lblTitle, 0, 0, 2, 1); // Title spans across two columns
        gridPane.add(lblText, 0, 1, 2, 1);
        gridPane.add(txtUsername, 0, 2, 2, 1);
        gridPane.add(txtPassword, 0, 3, 2, 1);
        gridPane.add(txtPasswordConfirm, 0, 4, 2, 1);
        gridPane.add(txtName, 0, 5, 2, 1);
        gridPane.add(txtSurname, 0, 6, 2, 1);
        gridPane.add(txtEmail, 0, 7, 2, 1);
        gridPane.add(txtPhone, 0, 8, 2, 1);
        gridPane.add(btnSignUp, 0, 11, 2, 1);

        // Wrap GridPane inside a StackPane to keep it centered horizontally and lower vertically
        StackPane stackPane = new StackPane(gridPane);
        stackPane.setAlignment(Pos.TOP_CENTER); // Set it to align top-center
        stackPane.setPadding(new Insets(200, 0, 0, 0)); // Add padding to push it lower vertically
        stackPane.prefWidthProperty().bind(mainAnchorPane.widthProperty());
        stackPane.prefHeightProperty().bind(mainAnchorPane.heightProperty());

        mainAnchorPane.getChildren().add(stackPane);

        btnSignUp.setOnMouseClicked(_ ->SignUp());
    }

    private void SignUp() {
        try {
            // Get the current stage and save its position and size
            Stage currentStage = (Stage) mainAnchorPane.getScene().getWindow();
            double currentWidth = currentStage.getWidth();
            double currentHeight = currentStage.getHeight();
            double currentX = currentStage.getX();
            double currentY = currentStage.getY();

            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-page-view.fxml")); // Replace with the actual path to the new FXML file
            Parent nextPage = loader.load();

            // Create a new scene with the loaded FXML
            Scene nextScene = new Scene(nextPage);

            // Set the stage to the previous size and position
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            stage.setX(currentX);
            stage.setY(currentY);

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

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("There was an issue opening the next page.");
        alert.showAndWait();
    }
}

package com.example.gbsproject;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class LoginPageViewController {
    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            // Now we can safely access the Stage
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();

            // Set the minimum width and height for the stage (window)
            stage.setMinWidth(1100);
            stage.setMaxWidth(2000);
            stage.setMinHeight(790);
        });


        // Create the GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER); // Ensure it is centered horizontally
        gridPane.setStyle("-fx-background-color: white;"); // Very light gray background
        gridPane.setMinSize(450, 420);
        gridPane.setMaxSize(450, 420);

        // Create UI elements
        Font customFont = Font.font("Droid Sans Mono Dotted", 16);
        Font customFont2 = Font.font("Droid Sans Mono Dotted", 14);
        Font titleFont = Font.font("Droid Sans Mono Dotted", 25);

        Label lblTitle = new Label("Log in");
        Label lblText = new Label("Please enter your username and password");
        Label lblText2 = new Label("Don't have an account yet? Click below to");
        lblTitle.setFont(titleFont);
        lblText.setFont(customFont);
        lblText2.setFont(customFont2);

        TextField txtUsername = new TextField();
        txtUsername.setFont(customFont);
        txtUsername.setPromptText("Username");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setFont(customFont);
        txtPassword.setPromptText("Password");

        Button btnLogin = new Button("Login");
        btnLogin.setFont(customFont);
        btnLogin.setStyle("-fx-background-color: rgba(2,93,11,0.8); -fx-text-fill: white;-fx-font-size: 16px; -fx-padding: 10px 15px;"); // Light green with white text

        Hyperlink emailLink = new Hyperlink("Sign up");
        Hyperlink link = new Hyperlink("Forgotten your username?");
        Hyperlink link2 = new Hyperlink("Forgotten your password?");
        link2.setStyle(" -fx-font-size: 14px; -fx-underline: true");
        link.setStyle(" -fx-font-size: 14px; -fx-underline: true");
        emailLink.setStyle(" -fx-font-size: 14px; -fx-underline: true");

        // Add elements to GridPane
        gridPane.add(lblTitle, 0, 0, 2, 1); // Title spans across two columns
        gridPane.add(lblText, 0, 1, 2, 1);
        gridPane.add(txtUsername, 0, 2, 2, 1);
        gridPane.add(txtPassword, 0, 3, 2, 1);
        gridPane.add(btnLogin, 0, 4, 2, 1);
        gridPane.add(link, 0, 5, 2, 1);
        gridPane.add(link2, 0, 6, 2, 1);
        gridPane.add(lblText2, 0, 8, 2, 1);
        gridPane.add(emailLink, 0, 9, 2, 1);

        // Wrap GridPane inside a StackPane to keep it centered horizontally and lower vertically
        StackPane stackPane = new StackPane(gridPane);
        stackPane.setAlignment(Pos.TOP_CENTER); // Set it to align top-center
        stackPane.setPadding(new Insets(300, 0, 0, 0)); // Add padding to push it lower vertically
        stackPane.prefWidthProperty().bind(mainAnchorPane.widthProperty());
        stackPane.prefHeightProperty().bind(mainAnchorPane.heightProperty());

        mainAnchorPane.getChildren().add(stackPane);
    }
}
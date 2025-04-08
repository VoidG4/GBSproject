package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.model.*;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.gbs.gbsproject.dao.UserDao;


public class LoginPageViewController {
    @FXML
    private AnchorPane mainAnchorPane;

    private static final Logger LOGGER = Logger.getLogger(LoginPageViewController.class.getName());


    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            // Now we can safely access the Stage
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();

            // Set the minimum width and height for the stage (window)
            stage.setMinWidth(1100);
            stage.setMaxWidth(2000);
            stage.setMinHeight(790);

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

        Hyperlink signUpText= new Hyperlink("Sign up");
        Hyperlink link = new Hyperlink("Forgotten your username?");
        Hyperlink link2 = new Hyperlink("Forgotten your password?");
        link2.setStyle(" -fx-font-size: 14px; -fx-underline: true");
        link.setStyle(" -fx-font-size: 14px; -fx-underline: true");
        signUpText.setStyle(" -fx-font-size: 14px; -fx-underline: true");

        // Add elements to GridPane
        gridPane.add(lblTitle, 0, 0, 2, 1); // Title spans across two columns
        gridPane.add(lblText, 0, 1, 2, 1);
        gridPane.add(txtUsername, 0, 2, 2, 1);
        gridPane.add(txtPassword, 0, 3, 2, 1);
        gridPane.add(btnLogin, 0, 4, 2, 1);
        gridPane.add(link, 0, 5, 2, 1);
        gridPane.add(link2, 0, 6, 2, 1);
        gridPane.add(lblText2, 0, 8, 2, 1);
        gridPane.add(signUpText, 0, 9, 2, 1);

        // Wrap GridPane inside a StackPane to keep it centered horizontally and lower vertically
        StackPane stackPane = new StackPane(gridPane);
        stackPane.setAlignment(Pos.TOP_CENTER); // Set it to align top-center
        stackPane.setPadding(new Insets(300, 0, 0, 0)); // Add padding to push it lower vertically
        stackPane.prefWidthProperty().bind(mainAnchorPane.widthProperty());
        stackPane.prefHeightProperty().bind(mainAnchorPane.heightProperty());

        mainAnchorPane.getChildren().add(stackPane);
        mainAnchorPane.setOnMouseClicked(_ -> formClicked());

        signUpText.setOnMouseClicked(_ ->SignUp());

        btnLogin.setOnMouseClicked(_ -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            if (username.isEmpty() || password.isEmpty()) {
                txtUsername.setStyle("-fx-border-color: red;");
                txtPassword.setStyle("-fx-border-color: red;");
                return;
            }

            try {
                User user = UserDao.checkLogin(username, password); // Get the logged-in user object

                if (user != null) {
                    Login(user); // Pass the user object to the Login method
                } else {
                    System.out.println("login failed: invalid credentials.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "An error occurred in the connection", e);
            }
        });
    }

    @FXML
    private void formClicked(){
        // Request focus on the mainAnchorPane to remove focus from text fields
        mainAnchorPane.requestFocus();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/sign-up-page-view.fxml")); // Replace with the actual path to the new FXML file
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
        }
    }

    private void Login(User user) {
        try {
            FXMLLoader loader;
            Parent nextPage;
            Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
            Scene nextScene;

            switch (user) {
                case Admin admin -> {
                    loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/admin-page-view.fxml"));
                    nextPage = loader.load();
                    AdminPageViewController adminController = loader.getController();
                    adminController.setAdmin(admin); // Pass Admin object to the controller

                    nextScene = new Scene(nextPage);
                }
                case Tutor tutor -> {
                    loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/tutor-page-view.fxml"));
                    nextPage = loader.load();
                    TutorPageViewController tutorController = loader.getController();
                    tutorController.setTutor(tutor); // Pass Tutor object to the controller
                    nextScene = new Scene(nextPage);
                }
                case Student student -> {
                    loader = new FXMLLoader(getClass().getResource("/com/gbs/gbsproject/fxml/home-page-view.fxml"));
                    nextPage = loader.load();
                    HomePageViewController studentController = loader.getController();
                    studentController.setStudent(student); // Pass Student object to the controller
                    nextScene = new Scene(nextPage);
                }
                case null, default -> {
                    return; // If no valid user, return
                }
            }

            // Set the stage to the previous size and position
            stage.setWidth(1600);
            stage.setHeight(1000);
            stage.centerOnScreen();

            // Set the new scene and show the stage
            stage.setScene(nextScene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while loading the next FXML", e);
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
}
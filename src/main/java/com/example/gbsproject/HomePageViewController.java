package com.example.gbsproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

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
            accountPane.setVisible(false);
            studiesPane.setVisible(false);
        }
    }

    @FXML
    protected void logOut() {
        try {
            // Load the FXML file for the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-page-view.fxml"));
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
    protected  void homeButtonClick(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-page-view.fxml"));
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
}

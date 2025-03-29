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


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePageViewController {

    private static final Logger LOGGER = Logger.getLogger(HomePageViewController.class.getName());
    public Button buttonMenuAccount;
    public AnchorPane mainAnchorPane;
    public Button buttonMenuStudies;

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

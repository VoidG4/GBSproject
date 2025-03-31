package com.example.gbsproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GBSApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GBSApplication.class.getResource("login-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);
        stage.setTitle("GBS");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
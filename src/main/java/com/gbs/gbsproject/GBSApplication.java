package com.gbs.gbsproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.gbs.gbsproject.util.DatabaseUtil;
public class GBSApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Connection conn;
        try {
            conn = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (conn != null) {
            System.out.println("Connection successful!");
            DatabaseUtil.closeConnection(conn);
        } else {
            System.out.println("Failed to connect to database.");
        }

        //TTSService.generateAndPlaySpeech();
        FXMLLoader fxmlLoader = new FXMLLoader(GBSApplication.class.getResource("/com/gbs/gbsproject/fxml/login-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);
        stage.setTitle("GBS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
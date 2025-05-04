package com.gbs.gbsproject.controller;

import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.service.GeminiService;
import com.gbs.gbsproject.service.STTService;
import com.gbs.gbsproject.service.TTSService;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class VoiceViewController {
    Student student;
    public StackPane rootPane;
    @FXML private Circle outerCircle;
    @FXML private Circle midCircle;
    @FXML private Circle innerCircle;
    @FXML private Text centerLabel;

    private boolean isListening = false;

    public void initialize() {
        pulseCircle(outerCircle);
        pulseCircle(midCircle);
        pulseCircle(innerCircle);

        // Enable click feedback
        outerCircle.setOnMouseClicked(this::toggleListening);
        midCircle.setOnMouseClicked(this::toggleListening);
        innerCircle.setOnMouseClicked(this::toggleListening);
        centerLabel.setOnMouseClicked(this::toggleListening);
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    private void pulseCircle(Circle circle) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), circle);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.25);
        scale.setToY(1.25);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.play();
    }

    private void toggleListening(MouseEvent event) {
        isListening = !isListening;

        if (isListening) {
            Future<String> futureResult = STTService.recognizeSpeech();

            System.out.println("Listening...");

            // Block until the Python script finishes and returns the result
            String recognizedText;  // You can add a timeout if desired
            try {
                recognizedText = futureResult.get();
                System.out.println(recognizedText);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            try {
                String response = GeminiService.askGemini(recognizedText);
                TTSService.generateSpeech(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            outerCircle.setFill(Color.web("#0D47A1"));  // Deep blue
            midCircle.setFill(Color.web("#1565C0"));
            innerCircle.setFill(Color.web("#1E88E5"));
            centerLabel.setText("Click if you \n heard the answer.");
            centerLabel.setTextAlignment(TextAlignment.CENTER); // Aligns multi-line text
        } else {
            outerCircle.setFill(Color.web("#D0F0FF"));
            midCircle.setFill(Color.web("#E0F7FA"));
            innerCircle.setFill(Color.web("#FFFFFF"));
            centerLabel.setText("Click to talk.");
        }
    }
}

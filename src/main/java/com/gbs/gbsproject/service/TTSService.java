package com.gbs.gbsproject.service;

import com.gbs.gbsproject.util.ElevenApiConfig;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import javazoom.jl.player.Player;
import org.jetbrains.annotations.NotNull;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TTSService {
    private static final Logger LOGGER = Logger.getLogger(TTSService.class.getName());

    private static Player currentPlayer;  // Track the player instance
    private static final AtomicBoolean isPlaying = new AtomicBoolean(false);

    // Generate Speech
    public static void generateSpeech(String text) {
        if (isPlaying.get()) {
            System.out.println("Speech is already playing.");
            return;
        }

        text = text.replaceAll("[^\\w\\s.,!]", "");


        try {
            HttpResponse<InputStream> response = Unirest.post(ElevenApiConfig.getApiUrl())
                    .header("xi-api-key", ElevenApiConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .body("{\n" +
                            "  \"text\": \"" + text + "\",\n" +
                            "  \"model_id\": \"eleven_multilingual_v2\"\n" +
                            "}")
                    .asBinary(); // Use asBinary to get MP3 file

            if (response.getStatus() == 200) {
                byte[] audioData = response.getBody().readAllBytes();
                Thread playerThread = getPlayerThread(audioData);

                playerThread.start(); // Start the player in a separate thread
                isPlaying.set(true); // Set isPlaying flag to true
            } else {
                System.out.println("Failed with status: " + response.getStatus());
                System.out.println("Response: " + response.getBody());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }

    @NotNull
    private static Thread getPlayerThread(byte[] audioData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);

        return new Thread(() -> {
            try {
                currentPlayer = new Player(bais);
                currentPlayer.play(); // Play the MP3 in a separate thread

                isPlaying.set(false);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred ", e);
                isPlaying.set(false); // Make sure to reset if error occurs too

            }
        });
    }

    // Pause the speech
    public static void pauseSpeech() {
        if (currentPlayer != null && isPlaying.get()) {
            // Closing the player effectively stops it.
            currentPlayer.close();
            isPlaying.set(false);
            System.out.println("Speech paused.");
        }
    }
}

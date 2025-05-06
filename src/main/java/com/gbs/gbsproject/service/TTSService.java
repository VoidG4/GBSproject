package com.gbs.gbsproject.service;

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TTSService {
    private static final Logger LOGGER = Logger.getLogger(TTSService.class.getName());

    private static Process vlcProcess = null;  // VLC process for controlling playback
    private static boolean isPlaying = false;  // Track playback status
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();  // Executor for background thread
    private static OutputStream vlcOutputStream = null;  // To send commands to VLC process

    // Start the audio generation and play it using VLC in a background thread
    public static void generateSpeech(String text) {
        if (isPlaying) {
            System.out.println("Audio is already playing." + vlcOutputStream);
            return;
        }

        // Escape quotes in the input to prevent breaking the command
        String safeInput = text.replace("\"", "\\\"");

        // Execute the task in a background thread
        executorService.submit(() -> {
            // Construct the terminal command to generate speech
            String command = String.format("source pyenv/bin/activate && python3 tts_service.py \"%s\"", safeInput);

            // ProcessBuilder to execute the command in a shell
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

            // Optional: set working directory to where your Python script lives
            processBuilder.directory(new File("."));

            try {
                // Start the Python process that generates the speech
                Process process = processBuilder.start();
                process.waitFor();  // Wait for the Python script to finish generating the speech

                // After the speech is generated, play it using VLC (in dummy mode)
                String vlcCommand = "vlc --intf rc --rc-host=localhost:1234 --play-and-exit speech.mp3";
                vlcProcess = new ProcessBuilder("bash", "-c", vlcCommand).start();
                vlcOutputStream = vlcProcess.getOutputStream();  // Get output stream for sending commands to VLC

                isPlaying = true;
                System.out.println("Audio started playing.");

                // Monitor the VLC process to detect when it finishes
                vlcProcess.waitFor();  // Wait for VLC to finish playing the MP3
                isPlaying = false;

                System.out.println("Audio finished playing.");

                // Once VLC finishes, we can clean up and shut down the background thread

            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "An error occurred", e);
            }
        });
    }

    public static synchronized void stopAudio() {
        if (vlcProcess != null && vlcProcess.isAlive()) {
            try {
                // Stop the VLC process
                vlcProcess.destroy();  // This will stop the VLC playback and kill the process
                isPlaying = false;
                System.out.println("Audio stopped.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred", e);
            }
        } else {
            System.out.println("No audio is currently playing.");
        }
    }

    // Shutdown the ExecutorService when done
    public static void shutdown() {
        executorService.shutdown();
        System.out.println("Background thread and ExecutorService shut down.");
    }

    public static boolean getIsPlaying() {
        return isPlaying;
    }


    public static void setVlcProcess(Process vlcProcess) {
        TTSService.vlcProcess = vlcProcess;
    }
}

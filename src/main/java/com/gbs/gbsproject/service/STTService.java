package com.gbs.gbsproject.service;

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class STTService {
    private static final Logger LOGGER = Logger.getLogger(STTService.class.getName());
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Method to capture and return the recognized speech text
    public static Future<String> recognizeSpeech() {
        return executorService.submit(() -> {
            String command = "source pyenv/bin/activate && python3 stt_service.py";

            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.directory(new File("."));
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);  // 💥 Suppress native stderr


            try {
                Process process = processBuilder.start();

                // Read the standard output (Python script's recognized text)
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                // Read and log error stream if any
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    LOGGER.warning("Python error: " + line);
                }

                int exitCode = process.waitFor();
                System.out.println("STT script exited with code " + exitCode);

                return output.toString().trim();  // Return the result

            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "An error occurred during speech recognition", e);
                return "[ERROR] Speech recognition failed.";
            }
        });
    }

    // Shutdown when done
    public static void shutdown() {
        executorService.shutdown();
        System.out.println("STTService executor shut down.");
    }
}

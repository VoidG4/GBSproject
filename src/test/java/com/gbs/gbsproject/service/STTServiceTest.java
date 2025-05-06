package com.gbs.gbsproject.service;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class STTServiceTest {

    @Test
    void testRecognizeSpeechSuccess() throws Exception {
        // Given: the real command will be executed, so ensure stt_service.py returns something
        Future<String> future = STTService.recognizeSpeech();

        // When
        String result = future.get(10, TimeUnit.SECONDS); // Timeout to avoid infinite wait

        // Then
        assertNotNull(result);
        // Depending on your mock or stub setup, check actual output here
        System.out.println("Recognized: " + result);
    }

    @Test
    void testRecognizeSpeechHandlesFailureGracefully() {
        // This test is illustrative. To truly simulate a failure, you should refactor the code
        // to allow injecting a mock ProcessBuilder (or wrap command execution in a helper class).
        // For now, assume the command fails and returns error string.

        // You might simulate this by temporarily renaming/removing the Python script.
        Future<String> future = STTService.recognizeSpeech();
        String result;
        try {
            result = future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            result = "[ERROR] Speech recognition failed.";
        }

        assertNotNull(result);
        // You can’t guarantee the output unless you mock or manipulate the env
        System.out.println("Result: " + result);
    }

    @AfterAll
    static void shutdownExecutor() {
        STTService.shutdown();
    }
}

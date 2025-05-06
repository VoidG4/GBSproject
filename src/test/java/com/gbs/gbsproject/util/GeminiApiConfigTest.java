package com.gbs.gbsproject.util;

import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class GeminiApiConfigTest {

    private static final String CONFIG_FILE = "config.properties";
    private static final String TEST_API_KEY = "fake-test-api-key";

    @BeforeAll
    static void setupConfigFile() throws IOException {
        Properties props = new Properties();
        props.setProperty("gemini.api.key", TEST_API_KEY);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            props.store(writer, "Test properties");
        }
    }

    @Test
    void testApiKeyLoadedSuccessfully() {
        // Act
        String apiKey = GeminiApiConfig.getApiKey();

        // Assert
        assertNotNull(apiKey);
        assertEquals(TEST_API_KEY, apiKey);
    }

    @AfterAll
    static void cleanupConfigFile() throws IOException {
        Files.deleteIfExists(Paths.get(CONFIG_FILE));
    }
}

package com.gbs.gbsproject.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
public class GeminiApiConfig {
    private static final String API_KEY;
    private static final Logger LOGGER = Logger.getLogger(GeminiApiConfig.class.getName());
    static {
        try {
            // Load properties file
            Properties properties = new Properties();
            FileInputStream input = new FileInputStream("config.properties");
            properties.load(input);

            // Read the API key from the properties file
            API_KEY = properties.getProperty("gemini.api.key");

            if (API_KEY == null || API_KEY.isEmpty()) {
                throw new IllegalArgumentException("API key is missing in the config file");
            }

        } catch (IOException e) {
            // Log the exception using a logger instead of printStackTrace()
            LOGGER.log(Level.SEVERE, "An error occurred while loading the API key", e);

            throw new RuntimeException("Error loading the config.properties file", e);
        }
    }

    public static String getApiKey() {
        return API_KEY;
    }
}

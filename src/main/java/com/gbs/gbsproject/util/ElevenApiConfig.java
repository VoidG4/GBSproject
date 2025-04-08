package com.gbs.gbsproject.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElevenApiConfig {
    private static final String API_KEY;
    private static final String API_URL;
    private static final Logger LOGGER = Logger.getLogger(ElevenApiConfig.class.getName());

    static {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);

            API_KEY = properties.getProperty("eleven.api.key");
            API_URL = properties.getProperty("eleven.api.url");

            if (API_KEY == null || API_KEY.isEmpty()) {
                throw new IllegalArgumentException("API key is missing in the config file");
            }

            if (API_URL == null || API_URL.isEmpty()) {
                throw new IllegalArgumentException("API URL is missing in the config file");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load ElevenLabs configuration", e);
            throw new RuntimeException("Error loading ElevenLabs config file", e);
        }
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getApiUrl() {
        return API_URL;
    }
}

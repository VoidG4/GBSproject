package com.gbs.gbsproject.service;

import com.gbs.gbsproject.util.GeminiApiConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GeminiService {
    private static String API_URL;

    static {
        // Load API_URL from config.properties file
        Properties configProperties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            configProperties.load(input);
            API_URL = configProperties.getProperty("API_URL"); // Get the API_URL from the file
            if (API_URL == null || API_URL.isEmpty()) {
                throw new RuntimeException("API_URL is not defined in config.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }

    public static String askGemini(String question) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(createJsonBody(question).toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + GeminiApiConfig.getApiKey())  // Use the API key from GeminiApiConfig
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response: " + response);

            if (response.body() == null) throw new IOException("Response body is null");

            JSONObject responseJson = new JSONObject(response.body().string());
            return responseJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        }
    }

    private static JSONObject createJsonBody(String question) {
        JSONArray partsArray = new JSONArray().put(new JSONObject().put("text", question));
        JSONArray contentsArray = new JSONArray().put(new JSONObject().put("parts", partsArray));
        return new JSONObject().put("contents", contentsArray);
    }

    public static void setApiUrl(String API_URL) {
        GeminiService.API_URL = API_URL;
    }
}
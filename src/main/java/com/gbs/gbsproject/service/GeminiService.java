package com.gbs.gbsproject.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public static String askGemini(String question) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(createJsonBody(question).toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + ApiConfig.getApiKey())  // Use the API key from ApiConfig
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
}
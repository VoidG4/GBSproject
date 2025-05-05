package com.gbs.gbsproject.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeminiServiceTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Directly modify the API_URL field without reflection
        GeminiService.setApiUrl(mockWebServer.url("/gemini").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testAskGemini() throws IOException {
        String fakeResponse = """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "This is a test response."
                  }
                ]
              }
            }
          ]
        }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(fakeResponse)
                .addHeader("Content-Type", "application/json"));

        String response = GeminiService.askGemini("Hello?");
        assertEquals("This is a test response.", response);
    }
}

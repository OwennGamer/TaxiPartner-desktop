package com.partnertaxi.taxipartneradmin;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class ApiClientTest {
    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        setBaseUrl(server.url("/").toString());
        clearJwt();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
        clearJwt();
    }

    @Test
    void loginReturnsNullOnSuccess() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"success\",\"token\":\"abc\"}"));

        String result = ApiClient.login("user", "pass");
        assertNull(result);
    }

    @Test
    void loginReturnsMessageOnBadRequest() {
        server.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"message\":\"Missing data\"}"));

        String result = ApiClient.login("user", "pass");
        assertEquals("Missing data", result);
    }

    @Test
    void loginReturnsMessageOnUnauthorized() {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"message\":\"Unauthorized\"}"));

        String result = ApiClient.login("user", "pass");
        assertEquals("Unauthorized", result);
    }

    @Test
    void loginReturnsServerErrorMessage() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        String result = ApiClient.login("user", "pass");
        assertEquals("Server error", result);
    }

    private static void setBaseUrl(String url) throws Exception {
        Field field = ApiClient.class.getDeclaredField("BASE_URL");
        field.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, url);
    }

    private static void clearJwt() throws Exception {
        Field field = ApiClient.class.getDeclaredField("jwtToken");
        field.setAccessible(true);
        field.set(null, null);
    }
}

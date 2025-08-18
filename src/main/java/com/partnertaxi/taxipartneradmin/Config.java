package com.partnertaxi.taxipartneradmin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) {
            return;
        }
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
    }

    public static String getBaseUrl() {
        String env = System.getenv("API_BASE_URL");
        if (env != null && !env.isEmpty()) {
            return env;
        }
        if (!loaded) {
            load();
        }
        return properties.getProperty("api.baseUrl", "http://164.126.143.20:8444/api/");
    }
}

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

    public static String getUpdateCheckUrl() {
        String env = System.getenv("UPDATE_CHECK_URL");
        if (env != null && !env.isEmpty()) {
            return env;
        }
        if (!loaded) {
            load();
        }
        return properties.getProperty("update.checkUrl", "http://164.126.143.20:8444/api/desktop/latest");
    }

    public static String getAppVersion() {
        String env = System.getenv("APP_VERSION");
        if (env != null && !env.isEmpty()) {
            return env;
        }
        String pkgVersion = Config.class.getPackage() != null ? Config.class.getPackage().getImplementationVersion() : null;
        if (pkgVersion != null && !pkgVersion.isEmpty()) {
            return pkgVersion;
        }
        if (!loaded) {
            load();
        }
        return properties.getProperty("app.version", "1.0.0");
    }
}

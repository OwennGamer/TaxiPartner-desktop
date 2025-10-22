package com.partnertaxi.taxipartneradmin;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Pushes application errors and crashes to the server so that support can diagnose
 * issues by driver ID, vehicle registration and timestamp.
 */
public final class RemoteLogService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "remote-log-service");
            thread.setDaemon(true);
            return thread;
        }
    });

    private static volatile String driverId;
    private static volatile String licensePlate;

    private RemoteLogService() {
    }

    /**
     * Sets the active driver context. Use this whenever the admin panel knows which driver
     * is being processed so that future errors are tied to that identifier.
     */
    public static void setDriverContext(String newDriverId, String newLicensePlate) {
        driverId = normalize(newDriverId);
        licensePlate = normalize(newLicensePlate);
    }

    /**
     * Clears the driver context. Useful after finishing work with a specific driver.
     */
    public static void clearDriverContext() {
        driverId = null;
        licensePlate = null;
    }

    public static void logHandledException(String summary, Throwable throwable) {
        send("ERROR", summary, throwable, null, null);
    }

    public static void logWarning(String summary, String details) {
        send("WARN", summary, null, details, null);
    }

    public static void logCrash(String summary, Throwable throwable) {
        send("FATAL", summary, throwable, null, null);
    }

    public static void logMessage(String level, String summary, String details, JSONObject metadata) {
        send(level, summary, null, details, metadata);
    }

    private static void send(String level, String summary, Throwable throwable, String message, JSONObject metadata) {
        final String safeLevel = normalizeLevel(level);
        final String safeSummary = summary == null || summary.isBlank()
                ? (throwable != null && throwable.getMessage() != null ? throwable.getMessage() : "Nieznany błąd")
                : summary.trim();

        JSONObject json = new JSONObject();
        json.put("summary", safeSummary);
        if (message != null && !message.isBlank()) {
            json.put("message", message.trim());
        } else if (throwable != null && throwable.getMessage() != null) {
            json.put("message", throwable.getMessage());
        }
        if (throwable != null) {
            json.put("stacktrace", stackTraceToString(throwable));
        }
        json.put("level", safeLevel);
        json.put("source", "desktop");
        json.put("app_version", Config.getAppVersion());
        json.put("occurred_at", Instant.now().toString());

        String activeDriver = driverId;
        String activePlate = licensePlate;
        if (activeDriver != null) {
            json.put("driver_id", activeDriver);
        }
        if (activePlate != null) {
            json.put("license_plate", activePlate);
        }
        if (metadata != null) {
            json.put("metadata", metadata);
        }

        EXECUTOR.execute(() -> submit(json));
    }

    private static void submit(JSONObject payload) {
        RequestBody body = RequestBody.create(payload.toString(), JSON);
        Request.Builder builder = new Request.Builder()
                .url(Config.getBaseUrl() + "log_error.php")
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("Device-Id", "admin_panel");

        String token = ApiClient.getJwtToken();
        if (token != null && !token.isBlank()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request request = builder.build();
        try (Response ignored = CLIENT.newCall(request).execute()) {
            // Nothing else to do – we ignore failures to avoid loops.
        } catch (Exception ex) {
            // As a fallback write to stderr so that at least the console captures the error.
            System.err.println("RemoteLogService failed to submit log: " + ex.getMessage());
        }
    }

    private static String stackTraceToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        return sw.toString();
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeLevel(String level) {
        String candidate = level == null ? "ERROR" : level.toUpperCase();
        switch (candidate) {
            case "DEBUG":
            case "INFO":
            case "WARN":
            case "ERROR":
            case "FATAL":
                return candidate;
            default:
                return "ERROR";
        }
    }
}

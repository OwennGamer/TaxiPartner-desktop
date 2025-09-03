package com.partnertaxi.taxipartneradmin;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class Updater {
    private static final OkHttpClient client = new OkHttpClient();

    public static void checkForUpdates() {
        Request request = new Request.Builder()
                .url(Config.getUpdateCheckUrl())
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return;
            }
            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            String latestVersion = json.optString("version");
            String downloadUrl = json.optString("url");
            if (isNewerVersion(latestVersion, Config.getAppVersion())) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Dostępna jest nowa wersja (" + latestVersion + "). Czy zainstalować?",
                            ButtonType.OK, ButtonType.CANCEL);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        downloadAndInstall(downloadUrl);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");
        int max = Math.max(latestParts.length, currentParts.length);
        for (int i = 0; i < max; i++) {
            int l = i < latestParts.length ? parsePart(latestParts[i]) : 0;
            int c = i < currentParts.length ? parsePart(currentParts[i]) : 0;
            if (l > c) {
                return true;
            } else if (l < c) {
                return false;
            }
        }
        return false;
    }

    private static int parsePart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void downloadAndInstall(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                return;
            }
            Path tempFile = Files.createTempFile("taxipartner-update", ".msi");
            try (InputStream in = response.body().byteStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            new ProcessBuilder("msiexec", "/i", tempFile.toString()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

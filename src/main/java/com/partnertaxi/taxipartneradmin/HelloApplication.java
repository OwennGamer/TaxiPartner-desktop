package com.partnertaxi.taxipartneradmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    // Blok statyczny — wykona się przed launch()
    static {
        Config.load();
        // globalne locale na polskie
        Locale.setDefault(new Locale("pl", "PL"));
        // dodatkowo na wszelki wypadek nadpisujemy systemowe properties
        System.setProperty("user.language", "pl");
        System.setProperty("user.country", "PL");
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Ikona okna — najpierw próbujemy /icons/app-icon.png, a jeśli brak, szukamy /app-icon.png
        try {
            InputStream iconStream = HelloApplication.class.getResourceAsStream("/icons/app-icon.png");
            if (iconStream == null) {
                iconStream = HelloApplication.class.getResourceAsStream("/app-icon.png");
            }
            if (iconStream != null) {
                primaryStage.getIcons().add(new javafx.scene.image.Image(iconStream));
            } else {
                System.err.println("Uwaga: nie znaleziono ikony app-icon.png w zasobach.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Domyślny tytuł okna
        changeScene("login-view.fxml", "Partner Taxi");
        stage.setMaximized(true);
        stage.show();

        // Sprawdzenie aktualizacji w osobnym wątku, aby nie blokować wątku UI
        new Thread(Updater::checkForUpdates).start();
    }



    public static void changeScene(String fxmlFile) {
        changeScene(fxmlFile, "Partner Taxi");
    }

    public static void changeScene(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Objects.requireNonNull(HelloApplication.class.getResource(fxmlFile))
            );
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            HelloApplication.class.getResource("style.css")
                    ).toExternalForm()
            );
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

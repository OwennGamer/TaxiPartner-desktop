package com.partnertaxi.taxipartneradmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;      // <<< konieczny import
import java.util.Objects;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    // <<< ten blok wykona się jeszcze przed wywołaniem launch()
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
        changeScene("login-view.fxml", "Logowanie administratora");
        stage.setMaximized(true);
        stage.show();
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

package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AddVehicleController {

    @FXML
    private TextField regField;
    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField mileageField;
    @FXML
    private DatePicker ubezpieczenieDatePicker;
    @FXML
    private DatePicker przegladDatePicker;
    @FXML
    private CheckBox activeCheckbox;

    @FXML
    public void handleSave() {
        try {
            String rejestracja = regField.getText().trim();
            String marka = brandField.getText().trim();
            String model = modelField.getText().trim();
            String mileageText = mileageField.getText().trim();

            if (rejestracja.isEmpty() || marka.isEmpty() || model.isEmpty() || mileageText.isEmpty()) {
                showError("Wszystkie pola tekstowe muszą być wypełnione.");
                return;
            }

            int przebieg;
            try {
                przebieg = Integer.parseInt(mileageText);
                if (przebieg < 0) {
                    showError("Przebieg nie może być ujemny.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Przebieg musi być liczbą całkowitą.");
                return;
            }

            if (ubezpieczenieDatePicker.getValue() == null || przegladDatePicker.getValue() == null) {
                showError("Wybierz daty ubezpieczenia i przeglądu.");
                return;
            }

            String ubezpieczenie = ubezpieczenieDatePicker.getValue().toString();
            String przeglad = przegladDatePicker.getValue().toString();
            int aktywny = activeCheckbox.isSelected() ? 1 : 0;

            String postData = "rejestracja=" + URLEncoder.encode(rejestracja, "UTF-8")
                    + "&marka=" + URLEncoder.encode(marka, "UTF-8")
                    + "&model=" + URLEncoder.encode(model, "UTF-8")
                    + "&przebieg=" + przebieg
                    + "&ubezpieczenie_do=" + URLEncoder.encode(ubezpieczenie, "UTF-8")
                    + "&przeglad_do=" + URLEncoder.encode(przeglad, "UTF-8")
                    + "&aktywny=" + aktywny;

            URL url = new URL("http://164.126.143.20:8444/api/add_vehicle.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Stage stage = (Stage) regField.getScene().getWindow();
                stage.close();
            } else {
                showError("Błąd dodawania pojazdu. Kod: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Wystąpił błąd: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

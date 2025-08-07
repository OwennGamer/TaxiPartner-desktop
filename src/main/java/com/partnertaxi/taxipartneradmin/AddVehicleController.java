package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    @FXML private CheckBox inpostCheckbox;
    @FXML private CheckBox taxiCheckbox;
    @FXML private CheckBox gazCheckbox;
    @FXML private HBox taxiBox;
    @FXML private CheckBox taksometrCheckbox;
    @FXML private DatePicker legalizacjaDatePicker;
    @FXML private DatePicker homologacjaDatePicker;
    @FXML private ChoiceBox<String> firmaChoiceBox;
    @FXML private TextField firmaOtherField;
    @FXML private TextField formaWlasnosciField;
    @FXML private TextField numerPolisyField;

    @FXML
    private void initialize() {
        taxiBox.setVisible(false);
        homologacjaDatePicker.setVisible(false);
        firmaOtherField.setVisible(false);

        taxiCheckbox.setOnAction(e -> taxiBox.setVisible(taxiCheckbox.isSelected()));
        gazCheckbox.setOnAction(e -> homologacjaDatePicker.setVisible(gazCheckbox.isSelected()));
        firmaChoiceBox.getItems().addAll("FUN", "POLCAR", "LINKPOST", "BLS", "INNA");
        firmaChoiceBox.setOnAction(e -> firmaOtherField.setVisible("INNA".equals(firmaChoiceBox.getValue())));
        firmaChoiceBox.setValue("FUN");
    }

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

            int inpost = inpostCheckbox.isSelected() ? 1 : 0;
            int taxi = taxiCheckbox.isSelected() ? 1 : 0;
            int gaz = gazCheckbox.isSelected() ? 1 : 0;

            int taksometr = taksometrCheckbox.isSelected() ? 1 : 0;
            String legalizacja = "";
            if (taxi == 1) {
                if (legalizacjaDatePicker.getValue() == null) {
                    showError("Wybierz datę legalizacji taksometru.");
                    return;
                }
                legalizacja = legalizacjaDatePicker.getValue().toString();
            }

            String homologacja = "";
            if (gaz == 1) {
                if (homologacjaDatePicker.getValue() == null) {
                    showError("Wybierz datę homologacji LPG.");
                    return;
                }
                homologacja = homologacjaDatePicker.getValue().toString();
            }

            String firma = firmaChoiceBox.getValue();
            if (firma == null || firma.isEmpty()) {
                showError("Wybierz firmę.");
                return;
            }
            if ("INNA".equals(firma)) {
                firma = firmaOtherField.getText().trim();
                if (firma.isEmpty()) {
                    showError("Podaj nazwę firmy.");
                    return;
                }
            }

            String formaWlasnosci = formaWlasnosciField.getText().trim();
            String numerPolisy = numerPolisyField.getText().trim();
            if (formaWlasnosci.isEmpty() || numerPolisy.isEmpty()) {
                showError("Uzupełnij formę własności i numer polisy.");
                return;
            }

            String postData = "rejestracja=" + URLEncoder.encode(rejestracja, "UTF-8")
                    + "&marka=" + URLEncoder.encode(marka, "UTF-8")
                    + "&model=" + URLEncoder.encode(model, "UTF-8")
                    + "&przebieg=" + przebieg
                    + "&ubezpieczenie_do=" + URLEncoder.encode(ubezpieczenie, "UTF-8")
                    + "&przeglad_do=" + URLEncoder.encode(przeglad, "UTF-8")
                    + "&aktywny=" + aktywny
                    + "&inpost=" + inpost
                    + "&taxi=" + taxi
                    + "&gaz=" + gaz
                    + "&taksometr=" + taksometr
                    + "&legalizacja_taksometru_do=" + URLEncoder.encode(legalizacja, "UTF-8")
                    + "&homologacja_lpg_do=" + URLEncoder.encode(homologacja, "UTF-8")
                    + "&firma=" + URLEncoder.encode(firma, "UTF-8")
                    + "&forma_wlasnosci=" + URLEncoder.encode(formaWlasnosci, "UTF-8")
                    + "&numer_polisy=" + URLEncoder.encode(numerPolisy, "UTF-8");

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

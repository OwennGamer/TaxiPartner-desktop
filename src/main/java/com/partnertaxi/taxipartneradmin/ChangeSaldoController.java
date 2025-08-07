package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.Locale;

public class ChangeSaldoController {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> reasonBox;
    @FXML private TextField customReasonField;

    private String driverId;
    private Runnable onSuccess; // wywołanie po udanym zapisie

    @FXML
    private void initialize() {
        // Ukryj pole opisu, jeśli nie "Inny"
        customReasonField.setVisible(false);

        // TextFormatter pozwalający na przecinek jako separator dziesiętny
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        StringConverter<Number> converter = new NumberStringConverter(nf);
        amountField.setTextFormatter(new TextFormatter<>(converter, null));

        // Pokaż/ukryj customReasonField
        reasonBox.setOnAction(event -> {
            customReasonField.setVisible("Inny".equals(reasonBox.getValue()));
        });
    }

    public void setDriverId(String id) {
        this.driverId = id;
    }

    public void setOnSuccess(Runnable callback) {
        this.onSuccess = callback;
    }

    @FXML
    private void handleSave() {
        try {
            // Pobierz już sparsowaną wartość z TextFormattera
            float amount = ((Number)amountField.getTextFormatter().getValue()).floatValue();

            String reason = reasonBox.getValue();
            if (reason == null || reason.isEmpty()) {
                showAlert("Błąd", "Musisz wybrać powód zmiany salda.");
                return;
            }

            String description;
            if ("Inny".equals(reason)) {
                description = customReasonField.getText().trim();
                if (description.isEmpty()) {
                    showAlert("Błąd", "Podaj opis dla powodu 'Inny'.");
                    return;
                }
            } else {
                description = reason;
            }

            ApiClient.updateSaldo(driverId, amount, description);

            if (onSuccess != null) {
                onSuccess.run();
            }
            closeWindow();

        } catch (Exception e) {
            showAlert("Błąd", "Nieprawidłowa wartość kwoty.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

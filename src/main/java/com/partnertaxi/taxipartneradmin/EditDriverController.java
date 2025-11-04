package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.Locale;

public class EditDriverController {

    @FXML private TextField       nameField;
    @FXML private Label           nameFieldPlaceholder;
    @FXML private PasswordField   passwordField;
    @FXML private Label           passwordFieldPlaceholder;

    @FXML private ComboBox<String> statusBox;
    @FXML private ComboBox<String> roleBox;
    @FXML private ComboBox<String> fuelCostBox;

    @FXML private TextField       percentTurnoverField;
    @FXML private Label           percentTurnoverPlaceholder;

    @FXML private TextField       cardCommissionField;
    @FXML private Label           cardCommissionPlaceholder;

    @FXML private TextField       partnerCommissionField;
    @FXML private Label           partnerCommissionPlaceholder;

    @FXML private TextField       boltCommissionField;
    @FXML private Label           boltCommissionPlaceholder;

    @FXML private TextField       settlementLimitField;
    @FXML private Label           settlementLimitPlaceholder;

    @FXML private Button          saveButton;
    @FXML private Button          cancelButton;

    private Driver driver;

    @FXML
    private void initialize() {
        // 1) inicjalizacja ComboBoxów
        statusBox.getItems().addAll("aktywny", "nieaktywny");
        roleBox.getItems().addAll("kierowca", "flotowiec", "administrator");
        fuelCostBox.getItems().addAll("firma", "kierowca");

        // 2) placeholdery
        bindPlaceholder(nameField, nameFieldPlaceholder);
        bindPlaceholder(passwordField, passwordFieldPlaceholder);
        bindPlaceholder(percentTurnoverField, percentTurnoverPlaceholder);
        bindPlaceholder(cardCommissionField, cardCommissionPlaceholder);
        bindPlaceholder(partnerCommissionField, partnerCommissionPlaceholder);
        bindPlaceholder(boltCommissionField, boltCommissionPlaceholder);
        bindPlaceholder(settlementLimitField, settlementLimitPlaceholder);

        // 3) TextFormatter z przecinkiem zamiast kropki
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        StringConverter<Number> converter = new NumberStringConverter(nf);

        percentTurnoverField.setTextFormatter(new TextFormatter<>(converter, null));
        cardCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        partnerCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        boltCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        settlementLimitField.setTextFormatter(new TextFormatter<>(converter, null));
    }

    private void bindPlaceholder(TextInputControl field, Label placeholder) {
        placeholder.visibleProperty().bind(field.textProperty().isEmpty());
        placeholder.setOnMouseClicked(e -> field.requestFocus());
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
        nameField.setText(driver.getName());
        statusBox.setValue(driver.getStatus());
        roleBox.setValue(driver.getRola());
        fuelCostBox.setValue(driver.getFuelCostText());

        // Rzutujemy TextFormatter<?> na TextFormatter<Number> przed setValue
        @SuppressWarnings("unchecked")
        TextFormatter<Number> tfPercent = (TextFormatter<Number>) percentTurnoverField.getTextFormatter();
        tfPercent.setValue(driver.getPercentTurnover());

        @SuppressWarnings("unchecked")
        TextFormatter<Number> tfCard = (TextFormatter<Number>) cardCommissionField.getTextFormatter();
        tfCard.setValue(driver.getCardCommission());

        @SuppressWarnings("unchecked")
        TextFormatter<Number> tfPartner = (TextFormatter<Number>) partnerCommissionField.getTextFormatter();
        tfPartner.setValue(driver.getPartnerCommission());

        @SuppressWarnings("unchecked")
        TextFormatter<Number> tfBolt = (TextFormatter<Number>) boltCommissionField.getTextFormatter();
        tfBolt.setValue(driver.getBoltCommission());

        @SuppressWarnings("unchecked")
        TextFormatter<Number> tfSettle = (TextFormatter<Number>) settlementLimitField.getTextFormatter();
        tfSettle.setValue(driver.getSettlementLimit());
    }

    @FXML
    private void handleSave() {
        try {
            String[] nameParts = nameField.getText().trim().split(" ", 2);
            String imie      = nameParts.length > 0 ? nameParts[0] : "";
            String nazwisko  = nameParts.length > 1 ? nameParts[1] : "";

            String password  = passwordField.getText().trim();
            String status    = statusBox.getValue();
            String rola      = roleBox.getValue();
            float fuelCost   = fuelCostBox.getValue().equals("firma") ? 0f : 1f;

            // pobieramy sparsowane wartości z TextFormatterów
            @SuppressWarnings("unchecked")
            float percentTurnover = ((TextFormatter<Number>)percentTurnoverField.getTextFormatter()).getValue().floatValue();
            @SuppressWarnings("unchecked")
            float cardCommission  = ((TextFormatter<Number>)cardCommissionField.getTextFormatter()).getValue().floatValue();
            @SuppressWarnings("unchecked")
            float partnerCommission = ((TextFormatter<Number>)partnerCommissionField.getTextFormatter()).getValue().floatValue();
            @SuppressWarnings("unchecked")
            float boltCommission  = ((TextFormatter<Number>)boltCommissionField.getTextFormatter()).getValue().floatValue();
            @SuppressWarnings("unchecked")
            float settlementLimit = ((TextFormatter<Number>)settlementLimitField.getTextFormatter()).getValue().floatValue();

            // jeżeli brak nowego hasła, użyj starego
            String haslo = password.isEmpty() ? driver.getId() : password;

            ApiClient.updateDriver(
                    driver.getId(),
                    imie, nazwisko,
                    haslo,
                    status,
                    rola,
                    percentTurnover,
                    fuelCost,
                    cardCommission,
                    partnerCommission,
                    boltCommission,
                    settlementLimit
            );
            closeWindow();
        } catch (Exception ex) {
            showAlert("Błąd", "Nieprawidłowe dane wejściowe:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage st = (Stage) cancelButton.getScene().getWindow();
        st.close();
    }

    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

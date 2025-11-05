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

public class AddDriverController {

    @FXML private TextField       idField;
    @FXML private Label           idFieldPlaceholder;

    @FXML private TextField       nameField;
    @FXML private Label           nameFieldPlaceholder;

    @FXML private PasswordField   passwordField;
    @FXML private Label           passwordFieldPlaceholder;

    @FXML private TextField       saldoField;
    @FXML private Label           saldoFieldPlaceholder;

    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private ChoiceBox<String> roleChoiceBox;
    @FXML private ChoiceBox<String> fuelCostChoiceBox;

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

    @FXML private TextField       fixedCostsField;
    @FXML private Label

    @FXML private Button          saveButton;
    @FXML private Button          cancelButton;

    @FXML
    private void initialize() {
        // Inicjalizacja ChoiceBoxów
        statusChoiceBox.getItems().addAll("aktywny", "nieaktywny");
        statusChoiceBox.setValue("aktywny");
        roleChoiceBox.getItems().addAll("kierowca", "flotowiec", "administrator");
        roleChoiceBox.setValue("kierowca");
        fuelCostChoiceBox.getItems().addAll("firma", "kierowca");
        fuelCostChoiceBox.setValue("firma");

        // Bindowanie placeholderów
        bindPlaceholder(idField,           idFieldPlaceholder);
        bindPlaceholder(nameField,         nameFieldPlaceholder);
        bindPlaceholder(passwordField,     passwordFieldPlaceholder);
        bindPlaceholder(saldoField,        saldoFieldPlaceholder);
        bindPlaceholder(percentTurnoverField, percentTurnoverPlaceholder);
        bindPlaceholder(cardCommissionField,  cardCommissionPlaceholder);
        bindPlaceholder(partnerCommissionField, partnerCommissionPlaceholder);
        bindPlaceholder(boltCommissionField,    boltCommissionPlaceholder);
        bindPlaceholder(settlementLimitField,   settlementLimitPlaceholder);
        bindPlaceholder(fixedCostsField,        fixedCostsPlaceholder);

        // Formatter dla pól liczbowych z przecinkiem jako separatorem dziesiętnym
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        StringConverter<Number> converter = new NumberStringConverter(nf);

        // Ustawienie TextFormatterów
        percentTurnoverField.setTextFormatter(new TextFormatter<>(converter, null));
        cardCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        partnerCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        boltCommissionField.setTextFormatter(new TextFormatter<>(converter, null));
        settlementLimitField.setTextFormatter(new TextFormatter<>(converter, null));
        fixedCostsField.setTextFormatter(new TextFormatter<>(converter, null));
        saldoField.setTextFormatter(new TextFormatter<>(converter, null));
    }

    private void bindPlaceholder(TextInputControl field, Label placeholder) {
        placeholder.visibleProperty().bind(field.textProperty().isEmpty());
        placeholder.setOnMouseClicked(e -> field.requestFocus());
    }

    @FXML
    private void handleSave() {
        try {
            String id = idField.getText().trim();
            String[] nameParts = nameField.getText().trim().split(" ", 2);
            String imie = nameParts.length > 0 ? nameParts[0] : "";
            String nazwisko = nameParts.length > 1 ? nameParts[1] : "";

            String password = passwordField.getText().trim();
            String status = statusChoiceBox.getValue();
            String rola = roleChoiceBox.getValue();
            float fuelCost = fuelCostChoiceBox.getValue().equals("firma") ? 0f : 1f;

            // Pobieramy wartości już sparsowane przez TextFormatter
            float percentTurnover   = getNumericValue(percentTurnoverField);
            float cardCommission    = getNumericValue(cardCommissionField);
            float partnerCommission = getNumericValue(partnerCommissionField);
            float boltCommission    = getNumericValue(boltCommissionField);
            float settlementLimit   = getNumericValue(settlementLimitField);
            float fixedCosts        = getNumericValue(fixedCostsField);
            float saldo             = getNumericValue(saldoField);

            ApiClient.addMobileUser(
                    id, imie, nazwisko, password, status, rola,
                    percentTurnover, fuelCost,
                    cardCommission, partnerCommission,
                    boltCommission, settlementLimit, fixedCosts, saldo
            );
            closeWindow();
        } catch(Exception ex) {
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
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private float getNumericValue(TextField field) {
        TextFormatter<?> formatter = field.getTextFormatter();
        if (formatter != null) {
            Object value = formatter.getValue();
            if (value instanceof Number number) {
                return number.floatValue();
            }
        }
        String text = field.getText();
        if (text == null || text.isBlank()) {
            return 0f;
        }
        return Float.parseFloat(text.replace(',', '.'));
    }
}

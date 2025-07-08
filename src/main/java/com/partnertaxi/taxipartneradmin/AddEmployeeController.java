package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

public class AddEmployeeController {

    @FXML private TextField idField;
    @FXML private Label idFieldPlaceholder;

    @FXML private TextField nameField;
    @FXML private Label nameFieldPlaceholder;

    @FXML private TextField firmaField;
    @FXML private Label firmaFieldPlaceholder;

    @FXML private TextField rodzajUmowyField;
    @FXML private Label rodzajUmowyPlaceholder;

    @FXML private DatePicker dataUmowyPicker;

    @FXML private CheckBox dowodCheck;
    @FXML private CheckBox prawoJazdyCheck;
    @FXML private CheckBox niekaralnoscCheck;
    @FXML private CheckBox orzPsychCheck;
    @FXML private DatePicker dataBadaniaPsychPicker;
    @FXML private CheckBox orzLekCheck;
    @FXML private DatePicker dataBadanLekPicker;
    @FXML private CheckBox informacjaPpkCheck;
    @FXML private CheckBox rezygnacjaPpkCheck;

    @FXML private TextField formaWyplatyField;
    @FXML private Label formaWyplatyPlaceholder;

    @FXML private CheckBox wynDoRakCheck;
    @FXML private CheckBox zgodaNaPrzelewCheck;
    @FXML private CheckBox ryzykoZawodoweCheck;
    @FXML private CheckBox oswiadczenieZUSCheck;
    @FXML private CheckBox bhpCheck;
    @FXML private CheckBox regulaminPracyCheck;
    @FXML private CheckBox zasadyEwidencjiKasaCheck;
    @FXML private CheckBox pit2Check;
    @FXML private CheckBox oswArt188Check;
    @FXML private CheckBox rodoCheck;
    @FXML private CheckBox poraNocnaCheck;

    @FXML private TextField pitEmailField;
    @FXML private Label pitEmailPlaceholder;

    @FXML private TextField osobaKontaktowaField;
    @FXML private Label osobaKontaktowaPlaceholder;

    @FXML private TextField numerPrywatnyField;
    @FXML private Label numerPrywatnyPlaceholder;

    @FXML private TextField numerSluzbowyField;
    @FXML private Label numerSluzbowyPlaceholder;

    @FXML private TextField modelTelefonuField;
    @FXML private Label modelTelefonuPlaceholder;

    @FXML private TextField operatorField;
    @FXML private Label operatorPlaceholder;

    @FXML private DatePicker waznoscWizyPicker;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        bindPlaceholder(idField, idFieldPlaceholder);
        bindPlaceholder(nameField, nameFieldPlaceholder);
        bindPlaceholder(firmaField, firmaFieldPlaceholder);
        bindPlaceholder(rodzajUmowyField, rodzajUmowyPlaceholder);
        bindPlaceholder(formaWyplatyField, formaWyplatyPlaceholder);
        bindPlaceholder(pitEmailField, pitEmailPlaceholder);
        bindPlaceholder(osobaKontaktowaField, osobaKontaktowaPlaceholder);
        bindPlaceholder(numerPrywatnyField, numerPrywatnyPlaceholder);
        bindPlaceholder(numerSluzbowyField, numerSluzbowyPlaceholder);
        bindPlaceholder(modelTelefonuField, modelTelefonuPlaceholder);
        bindPlaceholder(operatorField, operatorPlaceholder);
    }

    private void bindPlaceholder(TextInputControl field, Label placeholder) {
        if (placeholder == null) return;
        placeholder.visibleProperty().bind(field.textProperty().isEmpty());
        placeholder.setOnMouseClicked(e -> field.requestFocus());
    }

    @FXML
    private void handleSave() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            if (id.isEmpty() || name.isEmpty()) {
                showAlert("Błąd", "ID i imię i nazwisko są wymagane.");
                return;
            }

            String firma = firmaField.getText().trim();
            String rodzajUmowy = rodzajUmowyField.getText().trim();
            if (dataUmowyPicker.getEditor().getText().trim().isEmpty()) {
                dataUmowyPicker.setValue(null);
            }
            String dataUmowy = dataUmowyPicker.getValue() != null ? dataUmowyPicker.getValue().toString() : null;

            boolean dowod = dowodCheck.isSelected();
            boolean prawoJazdy = prawoJazdyCheck.isSelected();
            boolean niekaralnosc = niekaralnoscCheck.isSelected();
            boolean orzPsych = orzPsychCheck.isSelected();
            if (dataBadaniaPsychPicker.getEditor().getText().trim().isEmpty()) {
                dataBadaniaPsychPicker.setValue(null);
            }
            String dataBadaniaPsych = dataBadaniaPsychPicker.getValue() != null ? dataBadaniaPsychPicker.getValue().toString() : null;
            boolean orzLek = orzLekCheck.isSelected();
            if (dataBadanLekPicker.getEditor().getText().trim().isEmpty()) {
                dataBadanLekPicker.setValue(null);
            }
            String dataBadanLek = dataBadanLekPicker.getValue() != null ? dataBadanLekPicker.getValue().toString() : null;
            boolean informacjaPpk = informacjaPpkCheck.isSelected();
            boolean rezygnacjaPpk = rezygnacjaPpkCheck.isSelected();

            String formaWyplaty = formaWyplatyField.getText().trim();
            boolean wynDoRak = wynDoRakCheck.isSelected();
            boolean zgodaNaPrzelew = zgodaNaPrzelewCheck.isSelected();
            boolean ryzykoZawodowe = ryzykoZawodoweCheck.isSelected();
            boolean oswiadczenieZUS = oswiadczenieZUSCheck.isSelected();
            boolean bhp = bhpCheck.isSelected();
            boolean regulaminPracy = regulaminPracyCheck.isSelected();
            boolean zasadyEwidencjiKasa = zasadyEwidencjiKasaCheck.isSelected();
            boolean pit2 = pit2Check.isSelected();
            boolean oswArt188 = oswArt188Check.isSelected();
            boolean rodo = rodoCheck.isSelected();
            boolean poraNocna = poraNocnaCheck.isSelected();

            String pitEmail = pitEmailField.getText().trim();
            String osobaKontaktowa = osobaKontaktowaField.getText().trim();
            String numerPrywatny = numerPrywatnyField.getText().trim();
            String numerSluzbowy = numerSluzbowyField.getText().trim();
            String modelTelefonu = modelTelefonuField.getText().trim();
            String operator = operatorField.getText().trim();
            if (waznoscWizyPicker.getEditor().getText().trim().isEmpty()) {
                waznoscWizyPicker.setValue(null);
            }
            String waznoscWizy = waznoscWizyPicker.getValue() != null ? waznoscWizyPicker.getValue().toString() : null;

            Employee e = new Employee(id, name, firma, rodzajUmowy, dataUmowy,
                    dowod, prawoJazdy, niekaralnosc, orzPsych, dataBadaniaPsych,
                    orzLek, dataBadanLek, informacjaPpk, rezygnacjaPpk, formaWyplaty,
                    wynDoRak, zgodaNaPrzelew, ryzykoZawodowe, oswiadczenieZUS, bhp,
                    regulaminPracy, zasadyEwidencjiKasa, pit2, oswArt188, rodo, poraNocna,
                    pitEmail, osobaKontaktowa, numerPrywatny, numerSluzbowy,
                    modelTelefonu, operator, waznoscWizy);

            ApiClient.addEmployee(e);
            ApiClient.getEmployees();
            closeWindow();
        } catch (Exception ex) {
            showAlert("Błąd", "Nieprawidłowe dane:\n" + ex.getMessage());
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
}

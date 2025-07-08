package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

public class EditEmployeeController {

    @FXML private TextField idField;

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

    private Employee employee;

    public void setEmployee(Employee e) {
        this.employee = e;
        idField.setText(e.getId());
        nameField.setText(e.getName());
        firmaField.setText(e.getFirma());
        rodzajUmowyField.setText(e.getRodzajUmowy());
        if (e.getDataUmowy() != null && !e.getDataUmowy().isEmpty())
            dataUmowyPicker.setValue(java.time.LocalDate.parse(e.getDataUmowy()));

        dowodCheck.setSelected(e.isDowod());
        prawoJazdyCheck.setSelected(e.isPrawoJazdy());
        niekaralnoscCheck.setSelected(e.isNiekaralnosc());
        orzPsychCheck.setSelected(e.isOrzeczeniePsychologiczne());
        if (e.getDataBadaniaPsychologicznego() != null && !e.getDataBadaniaPsychologicznego().isEmpty())
            dataBadaniaPsychPicker.setValue(java.time.LocalDate.parse(e.getDataBadaniaPsychologicznego()));
        orzLekCheck.setSelected(e.isOrzeczenieLekarskie());
        if (e.getDataBadanLekarskich() != null && !e.getDataBadanLekarskich().isEmpty())
            dataBadanLekPicker.setValue(java.time.LocalDate.parse(e.getDataBadanLekarskich()));
        informacjaPpkCheck.setSelected(e.isInformacjaPpk());
        rezygnacjaPpkCheck.setSelected(e.isRezygnacjaPpk());

        formaWyplatyField.setText(e.getFormaWyplaty());
        wynDoRakCheck.setSelected(e.isWynagrodzenieDoRakWlasnych());
        zgodaNaPrzelewCheck.setSelected(e.isZgodaNaPrzelew());
        ryzykoZawodoweCheck.setSelected(e.isRyzykoZawodowe());
        oswiadczenieZUSCheck.setSelected(e.isOswiadczenieZUS());
        bhpCheck.setSelected(e.isBhp());
        regulaminPracyCheck.setSelected(e.isRegulaminPracy());
        zasadyEwidencjiKasaCheck.setSelected(e.isZasadyEwidencjiKasa());
        pit2Check.setSelected(e.isPit2());
        oswArt188Check.setSelected(e.isOswiadczenieArt188KP());
        rodoCheck.setSelected(e.isRodo());
        poraNocnaCheck.setSelected(e.isPoraNocna());

        pitEmailField.setText(e.getPitEmail());
        osobaKontaktowaField.setText(e.getOsobaKontaktowa());
        numerPrywatnyField.setText(e.getNumerPrywatny());
        numerSluzbowyField.setText(e.getNumerSluzbowy());
        modelTelefonuField.setText(e.getModelTelefonuSluzbowego());
        operatorField.setText(e.getOperator());
        if (e.getWaznoscWizy() != null && !e.getWaznoscWizy().isEmpty())
            waznoscWizyPicker.setValue(java.time.LocalDate.parse(e.getWaznoscWizy()));
    }

    @FXML
    private void handleSave() {
        if (employee == null) {
            closeWindow();
            return;
        }
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Błąd", "Imię i nazwisko nie może być puste.");
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

            employee = new Employee(employee.getId(), name, firma, rodzajUmowy, dataUmowy,
                    dowod, prawoJazdy, niekaralnosc, orzPsych, dataBadaniaPsych,
                    orzLek, dataBadanLek, informacjaPpk, rezygnacjaPpk, formaWyplaty,
                    wynDoRak, zgodaNaPrzelew, ryzykoZawodowe, oswiadczenieZUS, bhp,
                    regulaminPracy, zasadyEwidencjiKasa, pit2, oswArt188, rodo, poraNocna,
                    pitEmail, osobaKontaktowa, numerPrywatny, numerSluzbowy,
                    modelTelefonu, operator, waznoscWizy);

            ApiClient.updateEmployee(employee);
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
        Alert a = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

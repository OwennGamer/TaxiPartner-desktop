package com.partnertaxi.taxipartneradmin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.BooleanProperty;
import java.util.function.Function;
import java.util.Objects;

import com.partnertaxi.taxipartneradmin.TableUtils;

public class EmployeesController {

    private static final String PREF_KEY_COLUMNS_ORDER = "employeesTable.columnsOrder";


    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, String>  colId;
    @FXML private TableColumn<Employee, String>  colName;
    @FXML private TableColumn<Employee, String>  colFirma;
    @FXML private TableColumn<Employee, String>  colRodzajUmowy;
    @FXML private TableColumn<Employee, String>  colDataUmowy;

    @FXML private TableColumn<Employee, Boolean> colDowod;
    @FXML private TableColumn<Employee, Boolean> colPrawoJazdy;
    @FXML private TableColumn<Employee, Boolean> colNiekaralnosc;
    @FXML private TableColumn<Employee, Boolean> colOrzeczeniePsychologiczne;
    @FXML private TableColumn<Employee, String>  colDataBadaniaPsych;
    @FXML private TableColumn<Employee, Boolean> colOrzeczenieLekarskie;
    @FXML private TableColumn<Employee, String>  colDataBadanLekarskich;
    @FXML private TableColumn<Employee, Boolean> colInformacjaPpk;
    @FXML private TableColumn<Employee, Boolean> colRezygnacjaPpk;
    @FXML private TableColumn<Employee, String>  colFormaWyplaty;
    @FXML private TableColumn<Employee, Boolean> colWynagrodzenieDoRakWlasnych;
    @FXML private TableColumn<Employee, Boolean> colZgodaNaPrzelew;
    @FXML private TableColumn<Employee, Boolean> colRyzykoZawodowe;
    @FXML private TableColumn<Employee, Boolean> colOswiadczenieZUS;
    @FXML private TableColumn<Employee, Boolean> colBhp;
    @FXML private TableColumn<Employee, Boolean> colRegulaminPracy;
    @FXML private TableColumn<Employee, Boolean> colZasadyEwidencjiKasa;
    @FXML private TableColumn<Employee, Boolean> colPit2;
    @FXML private TableColumn<Employee, Boolean> colOswiadczenieArt188KP;
    @FXML private TableColumn<Employee, Boolean> colRodo;
    @FXML private TableColumn<Employee, Boolean> colPoraNocna;
    @FXML private TableColumn<Employee, String>  colPitEmail;
    @FXML private TableColumn<Employee, String>  colOsobaKontaktowa;
    @FXML private TableColumn<Employee, String>  colNumerPrywatny;
    @FXML private TableColumn<Employee, String>  colNumerSluzbowy;
    @FXML private TableColumn<Employee, String>  colModelTelefonuSluzbowego;
    @FXML private TableColumn<Employee, String>  colOperator;
    @FXML private TableColumn<Employee, String>  colWaznoscWizy;

    @FXML
    public void initialize() {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (colName != null) {
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (colFirma != null)           colFirma.setCellValueFactory(new PropertyValueFactory<>("firma"));
        if (colRodzajUmowy != null)     colRodzajUmowy.setCellValueFactory(new PropertyValueFactory<>("rodzajUmowy"));
        if (colDataUmowy != null)       colDataUmowy.setCellValueFactory(new PropertyValueFactory<>("dataUmowy"));

        setupCheckBoxColumn(colDowod, Employee::dowodProperty);
        setupCheckBoxColumn(colPrawoJazdy, Employee::prawoJazdyProperty);
        setupCheckBoxColumn(colNiekaralnosc, Employee::niekaralnoscProperty);
        setupCheckBoxColumn(colOrzeczeniePsychologiczne, Employee::orzeczeniePsychologiczneProperty);
        if (colDataBadaniaPsych != null)
            colDataBadaniaPsych.setCellValueFactory(new PropertyValueFactory<>("dataBadaniaPsychologicznego"));
        setupCheckBoxColumn(colOrzeczenieLekarskie, Employee::orzeczenieLekarskieProperty);
        if (colDataBadanLekarskich != null)
            colDataBadanLekarskich.setCellValueFactory(new PropertyValueFactory<>("dataBadanLekarskich"));
        setupCheckBoxColumn(colInformacjaPpk, Employee::informacjaPpkProperty);
        setupCheckBoxColumn(colRezygnacjaPpk, Employee::rezygnacjaPpkProperty);
        if (colFormaWyplaty != null)    colFormaWyplaty.setCellValueFactory(new PropertyValueFactory<>("formaWyplaty"));
        setupCheckBoxColumn(colWynagrodzenieDoRakWlasnych, Employee::wynagrodzenieDoRakWlasnychProperty);
        setupCheckBoxColumn(colZgodaNaPrzelew, Employee::zgodaNaPrzelewProperty);
        setupCheckBoxColumn(colRyzykoZawodowe, Employee::ryzykoZawodoweProperty);
        setupCheckBoxColumn(colOswiadczenieZUS, Employee::oswiadczenieZUSProperty);
        setupCheckBoxColumn(colBhp, Employee::bhpProperty);
        setupCheckBoxColumn(colRegulaminPracy, Employee::regulaminPracyProperty);
        setupCheckBoxColumn(colZasadyEwidencjiKasa, Employee::zasadyEwidencjiKasaProperty);
        setupCheckBoxColumn(colPit2, Employee::pit2Property);
        setupCheckBoxColumn(colOswiadczenieArt188KP, Employee::oswiadczenieArt188KPProperty);
        setupCheckBoxColumn(colRodo, Employee::rodoProperty);
        setupCheckBoxColumn(colPoraNocna, Employee::poraNocnaProperty);
        if (colPitEmail != null)        colPitEmail.setCellValueFactory(new PropertyValueFactory<>("pitEmail"));
        if (colOsobaKontaktowa != null) colOsobaKontaktowa.setCellValueFactory(new PropertyValueFactory<>("osobaKontaktowa"));
        if (colNumerPrywatny != null)   colNumerPrywatny.setCellValueFactory(new PropertyValueFactory<>("numerPrywatny"));
        if (colNumerSluzbowy != null)   colNumerSluzbowy.setCellValueFactory(new PropertyValueFactory<>("numerSluzbowy"));
        if (colModelTelefonuSluzbowego != null) colModelTelefonuSluzbowego.setCellValueFactory(new PropertyValueFactory<>("modelTelefonuSluzbowego"));
        if (colOperator != null)        colOperator.setCellValueFactory(new PropertyValueFactory<>("operator"));
        if (colWaznoscWizy != null)     colWaznoscWizy.setCellValueFactory(new PropertyValueFactory<>("waznoscWizy"));

        employeesTable.getItems().setAll(ApiClient.getEmployees());


        TableUtils.enableColumnsOrderPersistence(employeesTable, EmployeesController.class, PREF_KEY_COLUMNS_ORDER);
    }

    @FXML
    public void handleAddEmployee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-employee-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Dodaj pracownika");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.showAndWait();
            employeesTable.getItems().setAll(ApiClient.getEmployees());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditEmployee(ActionEvent event) {
        Employee sel = employeesTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pracownika", ButtonType.OK).showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-employee-view.fxml"));
            Parent root = loader.load();
            EditEmployeeController ctrl = loader.getController();
            ctrl.setEmployee(sel);
            Stage stage = new Stage();
            stage.setTitle("Edytuj pracownika");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.showAndWait();
            employeesTable.getItems().setAll(ApiClient.getEmployees());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteEmployee(ActionEvent event) {
        Employee sel = employeesTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz pracownika", ButtonType.OK).showAndWait();
            return;
        }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                "Usunąć pracownika " + sel.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
        conf.setHeaderText(null);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                ApiClient.deleteEmployee(sel.getId());
                employeesTable.getItems().setAll(ApiClient.getEmployees());
            }
        });
    }

    private void setupCheckBoxColumn(TableColumn<Employee, Boolean> column,
                                     Function<Employee, BooleanProperty> extractor) {
        if (column == null) return;
        column.setCellValueFactory(cellData -> extractor.apply(cellData.getValue()));
        column.setCellFactory(col -> new TableCell<>() {
            private final CheckBox box = new CheckBox();
            {
                box.setDisable(true);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    box.setSelected(Boolean.TRUE.equals(item));
                    setGraphic(box);
                }
            }
        });
    }
}

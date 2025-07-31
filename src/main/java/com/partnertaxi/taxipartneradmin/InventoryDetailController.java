package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import java.util.Objects;

public class InventoryDetailController {

    @FXML private Label lblId;
    @FXML private Label lblRejestracja;
    @FXML private Label lblPrzebieg;
    @FXML private Label lblCzyste;
    @FXML private Label lblDataDodania;
    @FXML private TextArea txtUwagi;

    @FXML private Label lblKamizelkiQty;

    @FXML private CheckBox cbLicencja;
    @FXML private CheckBox cbLegalizacja;
    @FXML private CheckBox cbDowod;
    @FXML private CheckBox cbUbezpieczenie;
    @FXML private CheckBox cbKartaLotniskowa;
    @FXML private CheckBox cbGasnica;
    @FXML private CheckBox cbLewarek;
    @FXML private CheckBox cbTrojkat;
    @FXML private CheckBox cbKamizelka;

    @FXML private ImageView imgFront;
    @FXML private ImageView imgBack;
    @FXML private ImageView imgLeft;
    @FXML private ImageView imgRight;
    @FXML private ImageView imgDirt1;
    @FXML private ImageView imgDirt2;
    @FXML private ImageView imgDirt3;
    @FXML private ImageView imgDirt4;

    public void setRecordId(int id) {
        InventoryDetailRecord rec = ApiClient.getInventoryDetail(id);
        if (rec == null) return;

        lblId.setText(String.valueOf(rec.getId()));
        lblRejestracja.setText(rec.getRejestracja());
        lblPrzebieg.setText(String.valueOf(rec.getPrzebieg()));
        lblCzyste.setText(rec.isCzysteWewnatrz() ? "Tak" : "Nie");
        lblDataDodania.setText(rec.getDataDodania());
        txtUwagi.setText(rec.getUwagi() == null ? "" : rec.getUwagi());

        lblKamizelkiQty.setText(String.valueOf(rec.getKamizelkiQty()));

        cbLicencja.setSelected(rec.isLicencja());
        cbLegalizacja.setSelected(rec.isLegalizacja());
        cbDowod.setSelected(rec.isDowod());
        cbUbezpieczenie.setSelected(rec.isUbezpieczenie());
        cbKartaLotniskowa.setSelected(rec.isKartaLotniskowa());
        cbGasnica.setSelected(rec.isGasnica());
        cbLewarek.setSelected(rec.isLewarek());
        cbTrojkat.setSelected(rec.isTrojkat());
        cbKamizelka.setSelected(rec.isKamizelka());

        setupImageViewer(rec.getPhotoFront(), imgFront);
        setupImageViewer(rec.getPhotoBack(), imgBack);
        setupImageViewer(rec.getPhotoLeft(), imgLeft);
        setupImageViewer(rec.getPhotoRight(), imgRight);
        setupImageViewer(rec.getPhotoDirt1(), imgDirt1);
        setupImageViewer(rec.getPhotoDirt2(), imgDirt2);
        setupImageViewer(rec.getPhotoDirt3(), imgDirt3);
        setupImageViewer(rec.getPhotoDirt4(), imgDirt4);
    }

    private void setupImageViewer(String path, ImageView view) {
        if (path != null && !path.isEmpty()) {
            String url = "http://164.126.143.20:8444/" + path;
            // ustawiamy miniaturkę
            view.setImage(new Image(url, 100, 80, true, true));
            view.setStyle("-fx-cursor: hand;");
            // kliknięcie otwiera duże okno
            view.setOnMouseClicked(e -> openImageDialog(url));
        } else {
            view.setImage(null);
        }
    }

    private void openImageDialog(String imageUrl) {
        ImageView imageView = new ImageView(new Image(imageUrl, true));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);

        ScrollPane scrollPane = new ScrollPane(new StackPane(imageView));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle("Podgląd zdjęcia");
        Scene scene = new Scene(scrollPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}

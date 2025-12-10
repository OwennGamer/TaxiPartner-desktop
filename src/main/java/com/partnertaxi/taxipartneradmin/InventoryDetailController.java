package com.partnertaxi.taxipartneradmin;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
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

        List<String> vehiclePhotos = new ArrayList<>();
        List<String> dirtPhotos = new ArrayList<>();

        setupImageViewer(rec.getPhotoFront(), imgFront, vehiclePhotos);
        setupImageViewer(rec.getPhotoBack(), imgBack, vehiclePhotos);
        setupImageViewer(rec.getPhotoLeft(), imgLeft, vehiclePhotos);
        setupImageViewer(rec.getPhotoRight(), imgRight, vehiclePhotos);
        setupImageViewer(rec.getPhotoDirt1(), imgDirt1, dirtPhotos);
        setupImageViewer(rec.getPhotoDirt2(), imgDirt2, dirtPhotos);
        setupImageViewer(rec.getPhotoDirt3(), imgDirt3, dirtPhotos);
        setupImageViewer(rec.getPhotoDirt4(), imgDirt4, dirtPhotos);
    }

    private void setupImageViewer(String path, ImageView view, List<String> group) {
        if (path != null && !path.isEmpty()) {
            String url = "http://164.126.143.20:8444/" + path;
            // ustawiamy miniaturkę
            view.setImage(new Image(url, 100, 80, true, true));
            view.setStyle("-fx-cursor: hand;");
            // kliknięcie otwiera duże okno
            int index = group.size();
            group.add(url);
            view.setOnMouseClicked(e -> openImageDialog(group, index));
        } else {
            view.setImage(null);
            view.setOnMouseClicked(null);
            view.setStyle(null);
        }
    }

    private void openImageDialog(List<String> imageUrls, int startIndex) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        int safeIndex = Math.max(0, Math.min(startIndex, imageUrls.size() - 1));

        Stage stage = new Stage();
        stage.setTitle("Podgląd zdjęcia");

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);

        DoubleProperty rotationAngle = new SimpleDoubleProperty(0);
        imageView.rotateProperty().bind(rotationAngle);

        StackPane imageContainer = new StackPane(imageView);
        imageContainer.setStyle("-fx-background-color: black;");

        ScrollPane scrollPane = new ScrollPane(imageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        IntegerProperty currentIndex = new SimpleIntegerProperty(safeIndex);

        StackPane leftArrow = createArrowPane("\u25C0", () -> {
            if (currentIndex.get() > 0) {
                currentIndex.set(currentIndex.get() - 1);
            }
        });

        StackPane rightArrow = createArrowPane("\u25B6", () -> {
            if (currentIndex.get() < imageUrls.size() - 1) {
                currentIndex.set(currentIndex.get() + 1);
            }
        });

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #222; -fx-border-color: #444; -fx-border-width: 1 0 0 0;");

        Button rotateLeft = new Button("Obróć w lewo");
        rotateLeft.setOnAction(e -> rotationAngle.set((rotationAngle.get() - 90 + 360) % 360));

        Button rotateRight = new Button("Obróć w prawo");
        rotateRight.setOnAction(e -> rotationAngle.set((rotationAngle.get() + 90) % 360));

        controls.getChildren().addAll(rotateLeft, rotateRight);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(scrollPane);
        borderPane.setLeft(leftArrow);
        borderPane.setRight(rightArrow);
        borderPane.setBottom(controls);

        Runnable updateContent = () -> {
            imageView.setImage(new Image(imageUrls.get(currentIndex.get()), true));
            stage.setTitle(String.format("Podgląd zdjęcia (%d/%d)", currentIndex.get() + 1, imageUrls.size()));
            updateArrowState(leftArrow, currentIndex.get() > 0);
            updateArrowState(rightArrow, currentIndex.get() < imageUrls.size() - 1);
            rotationAngle.set(0);
        };

        currentIndex.addListener((obs, oldVal, newVal) -> updateContent.run());
        updateContent.run();

        Scene scene = new Scene(borderPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private StackPane createArrowPane(String symbol, Runnable action) {
        Label label = new Label(symbol);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 48px;");

        StackPane container = new StackPane(label);
        container.setAlignment(Pos.CENTER);
        container.setPrefWidth(80);
        container.setStyle("-fx-background-color: rgba(0, 0, 0, 0.25);");
        container.setCursor(Cursor.HAND);
        container.setOnMouseClicked(e -> {
            if (!container.isDisable()) {
                action.run();
            }
        });

        return container;
    }

    private void updateArrowState(StackPane arrowPane, boolean enabled) {
        arrowPane.setDisable(!enabled);
        arrowPane.setOpacity(enabled ? 1.0 : 0.3);
        arrowPane.setCursor(enabled ? Cursor.HAND : Cursor.DEFAULT);
    }
}

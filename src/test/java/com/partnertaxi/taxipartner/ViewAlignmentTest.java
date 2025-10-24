package com.partnertaxi.taxipartner;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

public class DriversViewAlignmentTest extends ApplicationTest {
    private TableView<?> driversTable;
    private HBox summaryRow;
    private BorderPane root;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/partnertaxi/taxipartneradmin/drivers-view.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        driversTable = lookup("#driversTable").query();
        summaryRow = lookup("#summaryRow").query();
    }

    @Test
    public void testSummaryAlignmentAfterReorder() {
        interact(() -> {
            ObservableList<TableColumn<?, ?>> cols = driversTable.getColumns();
            FXCollections.rotate(cols, 1);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertAligned();
    }

    @Test
    public void testSummaryAlignmentAfterResize() {
        interact(() -> root.setPrefWidth(root.getPrefWidth() + 200));
        WaitForAsyncUtils.waitForFxEvents();
        assertAligned();
    }

    private void assertAligned() {
        TableViewSkin<?> skin = (TableViewSkin<?>) driversTable.getSkin();
        TableHeaderRow headerRow = skin.getTableHeaderRow();
        Map<String, Node> nodes = new HashMap<>();
        for (Node n : summaryRow.getChildren()) {
            nodes.put(n.getId(), n);
        }
        for (TableColumn<?, ?> col : driversTable.getColumns()) {
            Node n = nodes.get(mapping(col.getId()));
            assertNotNull(n, "Node for column " + col.getId());
            Node header = headerRow.getColumnHeaderFor(col);
            assertEquals(header.localToScene(0,0).getX(), n.localToScene(0,0).getX(), 1.0, col.getId()+" x");
            assertEquals(header.getWidth(), n.getLayoutBounds().getWidth(), 1.0, col.getId()+" width");
        }
    }

    private String mapping(String columnId) {
        return switch (columnId) {
            case "idColumn" -> "idPlaceholder";
            case "nameColumn" -> "namePlaceholder";
            case "saldoColumn" -> "saldoSumLabel";
            case "statusColumn" -> "statusPlaceholder";
            case "vehiclePlateColumn" -> "vehiclePlatePlaceholder";
            case "fuelCostColumn" -> "fuelCostPlaceholder";
            case "fuelCostSumColumn" -> "fuelSumLabel";
            case "percentTurnoverColumn" -> "percentTurnoverPlaceholder";
            case "cardCommissionColumn" -> "cardCommissionPlaceholder";
            case "partnerCommissionColumn" -> "partnerCommissionPlaceholder";
            case "boltCommissionColumn" -> "boltCommissionPlaceholder";
            case "settlementLimitColumn" -> "settlementLimitPlaceholder";
            case "voucherCurrentColumn" -> "voucherCurrentSumLabel";
            case "voucherPreviousColumn" -> "voucherPreviousSumLabel";
            case "cardColumn" -> "cardSumLabel";
            case "cashColumn" -> "cashSumLabel";
            case "lotColumn" -> "lotSumLabel";
            case "turnoverColumn" -> "turnoverSumLabel";
            case "zlPerKmColumn" -> "zlPerKmAvgLabel";
            case "fuelPerTurnoverColumn" -> "fuelPerTurnoverAvgLabel";
            case "createdAtColumn" -> "createdAtPlaceholder";
            default -> columnId;
        };
    }
}

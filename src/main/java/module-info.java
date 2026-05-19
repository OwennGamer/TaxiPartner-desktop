module com.partnertaxi.taxipartneradmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    // Dodajemy moduł do obsługi bazy danych (JDBC)
    requires java.sql;

    // Dodajemy moduły do obsługi HTTP i JSON
    requires okhttp3;
    requires com.google.gson;
    requires kotlin.stdlib;
    requires org.controlsfx.controls;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    // Udostępniamy pakiety do obsługi FXML
    opens com.partnertaxi.taxipartneradmin to javafx.fxml;

    // Eksportujemy główny pakiet aplikacji
    exports com.partnertaxi.taxipartneradmin;
}

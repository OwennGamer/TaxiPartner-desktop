package com.partnertaxi.taxipartneradmin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController {

    @FXML
    private Button startButton;

    @FXML
    private void handleStartButton() {
        System.out.println("🔓 Zalogowano jako admin");
        HelloApplication.changeScene("drivers-view.fxml", "Zarządzanie kierowcami");
    }


    @FXML
    private void handleTestAddUser() {
        ApiClient.addMobileUser(
                "jan123",          // id kierowcy
                "Jan",             // imię
                "Kowalski",        // nazwisko
                "haslo123",        // hasło
                "aktywny",         // status (poprawiona kolejność!)
                "kierowca",        // rola (poprawiona kolejność!)
                40f,               // percentTurnover
                6.5f,              // fuelCost
                5f,                // cardCommission
                10f,               // partnerCommission
                15f,               // boltCommission
                200f,              // settlementLimit
                0f                 // saldo (dodany brakujący parametr)
        );
    }


}

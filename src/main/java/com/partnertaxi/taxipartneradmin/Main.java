package com.partnertaxi.taxipartneradmin;

import java.util.Locale;  // <<< importujemy Locale

public class Main {
    public static void main(String[] args) {
        // <<< poniżej ustawiamy globalnie polską kulturę formatu liczb
        Locale.setDefault(new Locale("pl", "PL"));

        boolean success = ApiClient.login("admin", "admin");

        if (success) {
            System.out.println("✅ Logowanie zakończone sukcesem.");
            System.out.println("Token JWT: " + ApiClient.getJwtToken());
        } else {
            System.out.println("❌ Logowanie nieudane.");
        }
    }
}

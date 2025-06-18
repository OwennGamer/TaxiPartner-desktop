package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://164.126.143.20:8444/api/";
    private static String jwtToken;

    public static String getJwtToken() {
        return jwtToken;
    }

    // 🔐 Logowanie administratora
    public static boolean login(String username, String password) {
        try {
            URL url = new URL(BASE_URL + "admin_login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JSONObject loginData = new JSONObject();
            loginData.put("username", username);
            loginData.put("password", password);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(loginData.toString().getBytes("utf-8"));
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            JSONObject json = new JSONObject(response.toString());
            if ("success".equals(json.getString("status"))) {
                jwtToken = json.getString("token");
                System.out.println("✅ Zalogowano jako " + username);
                return true;
            } else {
                System.out.println("❌ Błąd logowania: " + json.getString("message"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ➕ Dodawanie kierowcy lub flotowca
    public static void addMobileUser(String id, String imie, String nazwisko, String password,
                                     String status, String rola,
                                     float percentTurnover, float fuelCost,
                                     float cardCommission, float partnerCommission,
                                     float boltCommission, float settlementLimit, float saldo) {
        try {
            URL url = new URL(BASE_URL + "add_driver.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("imie", imie);
            json.put("nazwisko", nazwisko);
            json.put("password", password);
            json.put("status", status);
            json.put("rola", rola);
            json.put("percentTurnover", percentTurnover);
            json.put("fuelCost", fuelCost);
            json.put("cardCommission", cardCommission);
            json.put("partnerCommission", partnerCommission);
            json.put("boltCommission", boltCommission);
            json.put("settlementLimit", settlementLimit);
            json.put("saldo", saldo);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("✅ Użytkownik mobilny dodany przez API.");
            } else {
                System.out.println("❌ Błąd dodawania użytkownika. Kod: " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✏️ Edycja kierowcy
    public static void updateDriver(String id, String imie, String nazwisko, String password,
                                    String status, float percentTurnover,
                                    float fuelCost, float cardCommission,
                                    float partnerCommission, float boltCommission,
                                    float settlementLimit) {
        try {
            URL url = new URL(BASE_URL + "update_driver.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("imie", imie);
            json.put("nazwisko", nazwisko);
            json.put("password", password);
            json.put("status", status);
            json.put("percentTurnover", percentTurnover);
            json.put("fuelCost", fuelCost);
            json.put("cardCommission", cardCommission);
            json.put("partnerCommission", partnerCommission);
            json.put("boltCommission", boltCommission);
            json.put("settlementLimit", settlementLimit);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("✅ Edycja użytkownika zakończona sukcesem.");
            } else {
                System.out.println("❌ Błąd edycji użytkownika. Kod: " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ❌ Usuwanie kierowcy
    public static void deleteDriver(String id) {
        try {
            URL url = new URL(BASE_URL + "delete_driver.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", id);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("✅ Kierowca usunięty.");
            } else {
                System.out.println("❌ Błąd usuwania kierowcy. Kod: " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔁 Zmiana salda kierowcy
    public static void updateSaldo(String driverId, float amount, String reason) {
        try {
            URL url = new URL(BASE_URL + "update_saldo.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", driverId);
            json.put("amount", amount);
            json.put("reason", reason);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("✅ Saldo zmienione pomyślnie.");
            } else {
                System.out.println("❌ Błąd zmiany salda. Kod: " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<HistoryEntry> getCombinedHistory(String driverId) {
        ObservableList<HistoryEntry> list = FXCollections.observableArrayList();
        try {
            URL url = new URL(BASE_URL + "get_combined_history.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("driverId", driverId);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JSONObject respJson = new JSONObject(response.toString());
            if ("success".equals(respJson.getString("status"))) {
                JSONArray arr = respJson.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    list.add(new HistoryEntry(
                            o.optString("date", ""),
                            o.optString("type", ""),
                            o.optString("description", ""),
                            o.opt("change") != JSONObject.NULL ? o.get("change").toString() : "0.00",
                            o.opt("saldo_po") != JSONObject.NULL ? o.get("saldo_po").toString() : "0.00"
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }






    // 🔎 GET helper
    public static String sendGetRequest(String endpoint) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            conn.disconnect();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Vehicle> getVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "get_vehicles.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());
            if ("success".equals(json.getString("status"))) {
                JSONArray arr = json.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    list.add(new Vehicle(
                            o.getInt("id"),
                            o.getString("rejestracja"),
                            o.getString("marka"),
                            o.getString("model"),
                            o.getInt("przebieg"),
                            o.getString("ubezpieczenie_do"),
                            o.getString("przeglad_do"),
                            o.getInt("aktywny") == 1
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<InventoryHistoryRecord> getInventoryHistory(String rejestracja) {
        List<InventoryHistoryRecord> list = new ArrayList<>();
        try {
            String endpoint = "get_inventory_history.php?rejestracja=" + URLEncoder.encode(rejestracja, "UTF-8");
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        list.add(new InventoryHistoryRecord(
                                o.getInt("id"),
                                o.getString("datetime"),
                                o.getInt("przebieg"),
                                o.getString("kierowca_id")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static InventoryDetailRecord getInventoryDetail(int id) {
        try {
            String json = sendGetRequest("get_inventory_detail.php?id=" + id);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONObject o = resp.getJSONObject("data");
                    return new InventoryDetailRecord(
                            o.getInt("id"),
                            o.getString("rejestracja"),
                            o.getInt("przebieg"),
                            o.getInt("czyste_wewnatrz") == 1,
                            o.getString("data_dodania"),
                            o.optString("photo_front", null),
                            o.optString("photo_back", null),
                            o.optString("photo_left", null),
                            o.optString("photo_right", null),
                            o.isNull("kamizelki_qty") ? 0 : o.getInt("kamizelki_qty"),
                            o.optString("photo_dirt1", null),
                            o.optString("photo_dirt2", null),
                            o.optString("photo_dirt3", null),
                            o.optString("photo_dirt4", null),
                            o.getInt("licencja") == 1,
                            o.getInt("legalizacja") == 1,
                            o.getInt("dowod") == 1,
                            o.getInt("ubezpieczenie") == 1,
                            o.getInt("karta_lotniskowa") == 1,
                            o.getInt("gasnica") == 1,
                            o.getInt("lewarek") == 1,
                            o.getInt("trojkat") == 1,
                            o.getInt("kamizelka") == 1,
                            o.optString("uwagi", null)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

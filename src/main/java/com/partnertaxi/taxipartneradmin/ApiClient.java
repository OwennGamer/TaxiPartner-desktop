package com.partnertaxi.taxipartneradmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = Config.getBaseUrl();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    private static String jwtToken;

    public static String getJwtToken() {
        return jwtToken;
    }

    private static class ApiResult {
        final int code;
        final String body;

        ApiResult(int code, String body) {
            this.code = code;
            this.body = body;
        }
    }

    private static ApiResult executeRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            return new ApiResult(response.code(), respBody);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(-1, null);
        }
    }

    private static ApiResult sendJsonPost(String endpoint, JSONObject json) {
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .addHeader("Accept", "application/json");
        if (jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return executeRequest(builder.build());
    }

    private static ApiResult sendPostRequest(String endpoint, String body) {
        RequestBody requestBody = RequestBody.create(body, FORM);
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(requestBody)
                .addHeader("Accept", "application/json");
        if (jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return executeRequest(builder.build());
    }

    private static ApiResult sendMultipartPost(String endpoint, MultipartBody.Builder bodyBuilder) {
        RequestBody body = bodyBuilder.setType(MultipartBody.FORM).build();
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .addHeader("Accept", "application/json");
        if (jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return executeRequest(builder.build());
    }

    public static String sendGetRequest(String endpoint) {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .addHeader("Accept", "application/json");
        if (jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return executeRequest(builder.build()).body;
    }

    // 🔐 Logowanie administratora
    public static String login(String username, String password) {
        try {
            JSONObject loginData = new JSONObject();
            loginData.put("username", username);
            loginData.put("password", password);

            RequestBody body = RequestBody.create(loginData.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "admin_login.php")
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .build();

            ApiResult result = executeRequest(request);
            JSONObject json = result.body != null && !result.body.isEmpty()
                    ? new JSONObject(result.body)
                    : new JSONObject();

            if (result.code == 200 && "success".equals(json.optString("status"))) {
                jwtToken = json.getString("token");
                return null;
            }

            if (result.code == 400) {
                return json.optString("message", "Brak wymaganych danych");
            } else if (result.code == 401) {
                return json.optString("message", "Nieprawidłowy login lub hasło");
            } else if (result.code >= 500) {
                String message = json.optString("message");
                if (!message.isEmpty()) {
                    return message;
                }
                String error = json.optString("error");
                if (!error.isEmpty()) {
                    return error;
                }
                return "Błąd serwera";
            } else {
                return json.optString("message", "Nieznany błąd");
            }
        } catch (Exception e) {
            return "Błąd połączenia z serwerem";
        }
    }

    // ➕ Dodawanie kierowcy lub flotowca
    public static void addMobileUser(String id, String imie, String nazwisko, String password,
                                     String status, String rola,
                                     float percentTurnover, float fuelCost,
                                     float cardCommission, float partnerCommission,
                                     float boltCommission, float settlementLimit, float saldo) {
        try {
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

            ApiResult res = sendJsonPost("add_driver.php", json);
            if (res.code == 200) {
                System.out.println("✅ Użytkownik mobilny dodany przez API.");
            } else {
                System.out.println("❌ Błąd dodawania użytkownika. Kod: " + res.code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✏️ Edycja kierowcy
    public static void updateDriver(String id, String imie, String nazwisko, String password,
                                    String status, String rola, float percentTurnover,
                                    float fuelCost, float cardCommission,
                                    float partnerCommission, float boltCommission,
                                    float settlementLimit) {
        try {
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

            ApiResult res = sendJsonPost("update_driver.php", json);
            if (res.code == 200) {
                System.out.println("✅ Edycja użytkownika zakończona sukcesem.");
            } else {
                System.out.println("❌ Błąd edycji użytkownika. Kod: " + res.code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ❌ Usuwanie kierowcy
    public static void deleteDriver(String id) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            ApiResult res = sendJsonPost("delete_driver.php", json);
            if (res.code == 200) {
                System.out.println("✅ Kierowca usunięty.");
            } else {
                System.out.println("❌ Błąd usuwania kierowcy. Kod: " + res.code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔁 Zmiana salda kierowcy
    public static void updateSaldo(String driverId, float amount, String reason) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", driverId);
            json.put("amount", amount);
            json.put("reason", reason);

            ApiResult res = sendJsonPost("update_saldo.php", json);
            String fcmStatus = "";
            if (res.body != null && !res.body.isEmpty()) {
                JSONObject respJson = new JSONObject(res.body);
                fcmStatus = respJson.optString("fcm_status", "");
            }

            if (res.code == 200) {
                System.out.println("✅ Saldo zmienione pomyślnie.");
            } else if (res.code == 207) {
                System.out.println("⚠️ Saldo zmienione, ale wysyłka FCM nie powiodła się.");
            } else {
                System.out.println("❌ Błąd zmiany salda. Kod: " + res.code);
                if (!fcmStatus.isEmpty()) {
                    System.out.println("ℹ️ fcm_status: " + fcmStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<HistoryEntry> getCombinedHistory(String driverId) {
        ObservableList<HistoryEntry> list = FXCollections.observableArrayList();
        try {
            JSONObject json = new JSONObject();
            json.put("driverId", driverId);

            ApiResult res = sendJsonPost("get_combined_history.php", json);
            if (res.body != null && !res.body.isEmpty()) {
                JSONObject respJson = new JSONObject(res.body);
                if ("success".equals(respJson.getString("status"))) {
                    JSONArray arr = respJson.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        String receiptPhoto = o.optString("receipt_photo", null);
                        String receiptPhotoUrl = (receiptPhoto != null && !receiptPhoto.isEmpty())
                                ? BASE_URL + receiptPhoto
                                : null;
                        boolean photoAvailable = o.optBoolean("photo_available", false);

                        list.add(new HistoryEntry(
                                o.optString("date", ""),
                                o.optString("type", ""),
                                o.optString("description", ""),
                                o.opt("change") != JSONObject.NULL ? o.get("change").toString() : "0.00",
                                o.opt("saldo_po") != JSONObject.NULL ? o.get("saldo_po").toString() : "0.00",
                                receiptPhotoUrl,
                                photoAvailable
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Vehicle> getVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            String resp = sendGetRequest("get_vehicles.php");
            if (resp != null) {
                JSONObject json = new JSONObject(resp);
                if ("success".equals(json.getString("status"))) {
                    JSONArray arr = json.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);

                        boolean aktywny = o.optBoolean("aktywny");
                        boolean inpost = o.optBoolean("inpost", o.optInt("inpost", 0) == 1);
                        boolean taxi = o.optBoolean("taxi", o.optInt("taxi", 0) == 1);
                        boolean taksometr = o.optBoolean("taksometr", o.optInt("taksometr", 0) == 1);
                        boolean gaz = o.optBoolean("gaz", o.optInt("gaz", 0) == 1);

                        list.add(new Vehicle(
                                o.getInt("id"),
                                o.getString("rejestracja"),
                                o.getString("marka"),
                                o.getString("model"),
                                o.getInt("przebieg"),
                                o.getString("ubezpieczenie_do"),
                                o.getString("przeglad_do"),
                                aktywny,
                                inpost,
                                taxi,
                                taksometr,
                                o.optString("legalizacja_taksometru_do", null),
                                gaz,
                                o.optString("homologacja_lpg_do", null),
                                o.optString("firma", null),
                                o.optString("forma_wlasnosci", null),
                                o.optString("numer_polisy", null)
                        ));
                    }
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

    public static List<ServiceRecord> getServiceRecords(String rejestracja) {
        List<ServiceRecord> list = new ArrayList<>();
        try {
            String endpoint = "get_service.php?rejestracja=" + URLEncoder.encode(rejestracja, "UTF-8");
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("services");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        list.add(new ServiceRecord(
                                o.getInt("id"),
                                o.optString("rejestracja", null),
                                o.optString("opis", null),
                                o.optDouble("koszt", 0.0),
                                o.optString("status", null),
                                o.optString("data", null),
                                parsePhotoArray(o.optJSONArray("zdjecia"))
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<DamageRecord> getDamageRecords(String rejestracja) {
        List<DamageRecord> list = new ArrayList<>();
        try {
            String endpoint = "get_damages.php?rejestracja=" + URLEncoder.encode(rejestracja, "UTF-8");
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("damages");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        list.add(new DamageRecord(
                                o.getInt("id"),
                                o.optString("rejestracja", null),
                                o.optString("nr_szkody", null),
                                o.optString("opis", null),
                                o.optString("status", null),
                                o.optDouble("koszt", 0.0),
                                o.optString("data", null),
                                parsePhotoArray(o.optJSONArray("zdjecia"))
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateServiceRecord(int id, String opis, double koszt) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("opis", opis);
            json.put("koszt", koszt);
            ApiResult res = sendJsonPost("update_service.php", json);
            return res.code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDamageRecord(int id, String opis, double koszt, String status, List<File> photos) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("id", String.valueOf(id))
                    .addFormDataPart("opis", opis)
                    .addFormDataPart("koszt", String.valueOf(koszt))
                    .addFormDataPart("status", status);
            if (photos != null) {
                for (File f : photos) {
                    builder.addFormDataPart("photos[]", f.getName(),
                            RequestBody.create(f, MediaType.get("image/jpeg")));
                }
            }
            ApiResult res = sendMultipartPost("update_damage.php", builder);
            return res.code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<String> parsePhotoArray(JSONArray arr) {
        List<String> photos = new ArrayList<>();
        if (arr != null) {
            String base = BASE_URL.replace("api/", "");
            for (int i = 0; i < arr.length(); i++) {
                String p = arr.optString(i, null);
                if (p != null && !p.isEmpty()) {
                    photos.add(base + p);
                }
            }
        }
        return photos;
    }

    /**
     * Pobiera statystyki kierowcy z API.
     */
    public static DriverStats getDriverStats(String driverId, String startDate, String endDate) {
        try {
            String endpoint = String.format(
                    "get_driver_stats.php?driver_id=%s&start_date=%s&end_date=%s",
                    URLEncoder.encode(driverId, "UTF-8"),
                    URLEncoder.encode(startDate, "UTF-8"),
                    URLEncoder.encode(endDate, "UTF-8")
            );
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONObject d = resp.getJSONObject("data");
                    return new DriverStats(
                            (float) d.optDouble("voucher", 0.0),
                            (float) d.optDouble("card", 0.0),
                            (float) d.optDouble("cash", 0.0),
                            (float) d.optDouble("lot", 0.0),
                            (float) d.optDouble("turnover", 0.0),
                            (float) d.optDouble("kilometers", 0.0),
                            (float) d.optDouble("fuel_sum", 0.0),
                            d.optBoolean("missing_mileage", false)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Pobiera statystyki dla wielu kierowców.
     * Zwracana lista odpowiada kolejności przekazanych identyfikatorów.
     */
    public static List<DriverStats> getDriverStats(List<String> driverIds, String startDate, String endDate) {
        List<DriverStats> result = new ArrayList<>();
        if (driverIds == null) {
            return result;
        }
        for (String id : driverIds) {
            result.add(getDriverStats(id, startDate, endDate));
        }
        return result;
    }

    /**
     * Pobiera zestawienie godzin pracy i przebiegu dla kierowcy.
     */
    public static List<DriverWorkEntry> getDriverWork(String driverId, String startDate, String endDate) {
        List<DriverWorkEntry> list = new ArrayList<>();
        try {
            String endpoint = String.format(
                    "get_driver_work.php?driver_id=%s&start_date=%s&end_date=%s",
                    URLEncoder.encode(driverId, "UTF-8"),
                    URLEncoder.encode(startDate, "UTF-8"),
                    URLEncoder.encode(endDate, "UTF-8")
            );
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        list.add(new DriverWorkEntry(
                                o.getString("date"),
                                (float) o.optDouble("hours", 0.0),
                                o.optInt("kilometers", 0)
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Pobiera pojedyncze sesje pracy kierowcy.
     */
    public static List<DriverWorkEntry> getDriverSessions(String driverId, String startDate, String endDate) {
        List<DriverWorkEntry> list = new ArrayList<>();
        try {
            String endpoint = String.format(
                    "get_driver_sessions.php?driver_id=%s&start_date=%s&end_date=%s",
                    URLEncoder.encode(driverId, "UTF-8"),
                    URLEncoder.encode(startDate, "UTF-8"),
                    URLEncoder.encode(endDate, "UTF-8")
            );
            String json = sendGetRequest(endpoint);
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        String startTime = o.optString("start_time", null);
                        String endTime   = o.optString("end_time", null);
                        int startOdometer = o.optInt("start_odometer", 0);
                        int endOdometer   = o.optInt("end_odometer", 0);

                        float hours = 0f;
                        int kilometers = 0;
                        try {
                            if (startTime != null && endTime != null) {
                                java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime.replace(" ", "T"));
                                java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime.replace(" ", "T"));
                                hours = java.time.Duration.between(start, end).toMinutes() / 60f;
                            }
                            kilometers = endOdometer - startOdometer;
                        } catch (Exception ignore) {}

                        String date = startTime != null ? startTime.substring(0, 10) : "";
                        list.add(new DriverWorkEntry(date, hours, kilometers, startTime, endTime));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Rozpoczyna sesję pracy kierowcy.
     * @return ID utworzonej sesji lub null w razie błędu.
     */
    public static Integer startShift(String vehiclePlate, int startOdometer) {
        try {
            String body = String.format("vehicle_plate=%s&start_odometer=%d",
                    URLEncoder.encode(vehiclePlate, "UTF-8"), startOdometer);
            ApiResult resp = sendPostRequest("start_shift.php", body);
            if (resp.body != null) {
                JSONObject json = new JSONObject(resp.body);
                if ("success".equals(json.optString("status"))) {
                    return json.optInt("session_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kończy najnowszą otwartą sesję pracy.
     * @return ID zakończonej sesji lub null w razie błędu.
     */
    public static Integer endShift(int endOdometer) {
        try {
            String body = "end_odometer=" + endOdometer;
            ApiResult resp = sendPostRequest("end_shift.php", body);
            if (resp.body != null) {
                JSONObject json = new JSONObject(resp.body);
                if ("success".equals(json.optString("status"))) {
                    return json.optInt("session_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✏️ Aktualizacja pojazdu
    public static void updateVehicle(int id, String rejestracja, String marka, String model,
                                     int przebieg, String ubezpieczenie, String przeglad,
                                     boolean aktywny, boolean inpost, boolean taxi, boolean taksometr,
                                     String legalizacja, boolean gaz, String homologacja,
                                     String firma, String formaWlasnosci, String numerPolisy) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("rejestracja", rejestracja);
            json.put("marka", marka);
            json.put("model", model);
            json.put("przebieg", przebieg);
            json.put("ubezpieczenie_do", ubezpieczenie);
            json.put("przeglad_do", przeglad);
            json.put("aktywny", aktywny ? 1 : 0);
            json.put("inpost", inpost ? 1 : 0);
            json.put("taxi", taxi ? 1 : 0);
            json.put("taksometr", taksometr ? 1 : 0);
            json.put("legalizacja_taksometru_do", legalizacja == null ? "" : legalizacja);
            json.put("gaz", gaz ? 1 : 0);
            json.put("homologacja_lpg_do", homologacja == null ? "" : homologacja);
            json.put("firma", firma == null ? "" : firma);
            json.put("forma_wlasnosci", formaWlasnosci == null ? "" : formaWlasnosci);
            json.put("numer_polisy", numerPolisy == null ? "" : numerPolisy);

            sendJsonPost("update_vehicle.php", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ❌ Usuwanie pojazdu
    public static void deleteVehicle(int id) {
        try {
            String postData = "id=" + id;
            sendPostRequest("delete_vehicle.php", postData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📄 Pobieranie listy pracowników
    public static List<Employee> getEmployees() {
        List<Employee> list = new ArrayList<>();
        try {
            String json = sendGetRequest("get_employees.php");
            if (json != null) {
                JSONObject resp = new JSONObject(json);
                if ("success".equals(resp.getString("status"))) {
                    JSONArray arr = resp.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        list.add(new Employee(
                                o.getString("id"),
                                o.getString("name"),
                                o.optString("firma", ""),
                                o.optString("rodzaj_umowy", ""),
                                o.optString("data_umowy", ""),
                                o.optInt("dowod", 0) == 1,
                                o.optInt("prawo_jazdy", 0) == 1,
                                o.optInt("niekaralnosc", 0) == 1,
                                o.optInt("orzeczenie_psychologiczne", 0) == 1,
                                o.optString("data_badania_psychologicznego", ""),
                                o.optInt("orzeczenie_lekarskie", 0) == 1,
                                o.optString("data_badan_lekarskich", ""),
                                o.optInt("informacja_ppk", 0) == 1,
                                o.optInt("rezygnacja_ppk", 0) == 1,
                                o.optString("forma_wyplaty", ""),
                                o.optInt("wynagrodzenie_do_rak_wlasnych", 0) == 1,
                                o.optInt("zgoda_na_przelew", 0) == 1,
                                o.optInt("ryzyko_zawodowe", 0) == 1,
                                o.optInt("oswiadczenie_zus", 0) == 1,
                                o.optInt("bhp", 0) == 1,
                                o.optInt("regulamin_pracy", 0) == 1,
                                o.optInt("zasady_ewidencji_kasa", 0) == 1,
                                o.optInt("pit2", 0) == 1,
                                o.optInt("oswiadczenie_art188_kp", 0) == 1,
                                o.optInt("rodo", 0) == 1,
                                o.optInt("pora_nocna", 0) == 1,
                                o.optString("pit_email", ""),
                                o.optString("osoba_kontaktowa", ""),
                                o.optString("numer_prywatny", ""),
                                o.optString("numer_sluzbowy", ""),
                                o.optString("model_telefonu_sluzbowego", ""),
                                o.optString("operator", ""),
                                o.optString("waznosc_wizy", "")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ➕ Dodawanie pracownika
    public static void addEmployee(Employee e) {
        try {
            JSONObject json = employeeToJson(e);
            sendJsonPost("add_employee.php", json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ✏️ Aktualizacja pracownika
    public static void updateEmployee(Employee e) {
        try {
            JSONObject json = employeeToJson(e);
            sendJsonPost("update_employee.php", json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ❌ Usuwanie pracownika
    public static void deleteEmployee(String id) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            sendJsonPost("delete_employee.php", json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static JSONObject employeeToJson(Employee e) {
        JSONObject json = new JSONObject();
        json.put("id", e.getId());
        json.put("name", e.getName());
        json.put("firma", e.getFirma());
        json.put("rodzaj_umowy", e.getRodzajUmowy());
        json.put("data_umowy", e.getDataUmowy());
        json.put("dowod", e.isDowod() ? 1 : 0);
        json.put("prawo_jazdy", e.isPrawoJazdy() ? 1 : 0);
        json.put("niekaralnosc", e.isNiekaralnosc() ? 1 : 0);
        json.put("orzeczenie_psychologiczne", e.isOrzeczeniePsychologiczne() ? 1 : 0);
        json.put("data_badania_psychologicznego", e.getDataBadaniaPsychologicznego());
        json.put("orzeczenie_lekarskie", e.isOrzeczenieLekarskie() ? 1 : 0);
        json.put("data_badan_lekarskich", e.getDataBadanLekarskich());
        json.put("informacja_ppk", e.isInformacjaPpk() ? 1 : 0);
        json.put("rezygnacja_ppk", e.isRezygnacjaPpk() ? 1 : 0);
        json.put("forma_wyplaty", e.getFormaWyplaty());
        json.put("wynagrodzenie_do_rak_wlasnych", e.isWynagrodzenieDoRakWlasnych() ? 1 : 0);
        json.put("zgoda_na_przelew", e.isZgodaNaPrzelew() ? 1 : 0);
        json.put("ryzyko_zawodowe", e.isRyzykoZawodowe() ? 1 : 0);
        json.put("oswiadczenie_zus", e.isOswiadczenieZUS() ? 1 : 0);
        json.put("bhp", e.isBhp() ? 1 : 0);
        json.put("regulamin_pracy", e.isRegulaminPracy() ? 1 : 0);
        json.put("zasady_ewidencji_kasa", e.isZasadyEwidencjiKasa() ? 1 : 0);
        json.put("pit2", e.isPit2() ? 1 : 0);
        json.put("oswiadczenie_art188_kp", e.isOswiadczenieArt188KP() ? 1 : 0);
        json.put("rodo", e.isRodo() ? 1 : 0);
        json.put("pora_nocna", e.isPoraNocna() ? 1 : 0);
        json.put("pit_email", e.getPitEmail());
        json.put("osoba_kontaktowa", e.getOsobaKontaktowa());
        json.put("numer_prywatny", e.getNumerPrywatny());
        json.put("numer_sluzbowy", e.getNumerSluzbowy());
        json.put("model_telefonu_sluzbowego", e.getModelTelefonuSluzbowego());
        json.put("operator", e.getOperator());
        json.put("waznosc_wizy", e.getWaznoscWizy());
        return json;
    }
}

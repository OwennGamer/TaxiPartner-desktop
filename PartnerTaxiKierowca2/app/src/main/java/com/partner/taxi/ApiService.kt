package com.partner.taxi

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Field

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("driver_id") driverId: String,
        @Field("password") password: String,
        @Field("device_id") deviceId: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("refresh_token.php")
    fun refreshToken(
        @Field("device_id") deviceId: String
    ): Call<RefreshTokenResponse>

    @GET("get_vehicle_info.php")
    fun getVehicleInfo(
        @Query("rejestracja") rejestracja: String
    ): Call<VehicleResponse>

    @GET("getDriverData.php")
    suspend fun getDriverData(
        @Query("driver_id") driverId: String
    ): Response<DriverDataResponse>

    @GET("get_vehicles.php")
    suspend fun getVehicles(): VehiclesResponse


    @GET("mobile_update.php")
    fun getMobileUpdate(
        @Query("platform") platform: String = "android"
    ): Call<MobileUpdateResponse>

    @FormUrlEncoded
    @POST("update_vehicle_mileage.php")
    fun updateVehicleMileage(
        @Field("rejestracja") rejestracja: String,
        @Field("przebieg") przebieg: Int
    ): Call<GenericResponse>

    @Multipart
    @POST("add_inventory.php")
    fun addInventory(
        @Part("rejestracja") rejestracja: RequestBody,
        @Part("przebieg") przebieg: RequestBody,
        @Part("czyste_wewnatrz") czysteWewnatrz: RequestBody,

        @Part photo_front: MultipartBody.Part?,
        @Part photo_back: MultipartBody.Part?,
        @Part photo_left: MultipartBody.Part?,
        @Part photo_right: MultipartBody.Part?,

        @Part photo_dirt1: MultipartBody.Part?,
        @Part photo_dirt2: MultipartBody.Part?,
        @Part photo_dirt3: MultipartBody.Part?,
        @Part photo_dirt4: MultipartBody.Part?,

        @Part("kamizelki_qty") kamizelkiQty: RequestBody,

        @Part("licencja") licencja: RequestBody,
        @Part("legalizacja") legalizacja: RequestBody,
        @Part("dowod") dowod: RequestBody,
        @Part("ubezpieczenie") ubezpieczenie: RequestBody,
        @Part("karta_lotniskowa") kartaLotniskowa: RequestBody,
        @Part("gasnica") gasnica: RequestBody,
        @Part("lewarek") lewarek: RequestBody,
        @Part("trojkat") trojkat: RequestBody,
        @Part("kamizelka") kamizelka: RequestBody,

        @Part("uwagi") uwagi: RequestBody?
    ): Call<GenericResponse>

    @Multipart
    @POST("addRide.php")
    fun addRide(
        @Part receipt: MultipartBody.Part?,
        @Part("driver_id") driverId: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("type") type: RequestBody,
        @Part("source") source: RequestBody,
        @Part("via_km") viaKm: RequestBody
    ): Call<AddRideResponse>

    @GET("getRideHistory.php")
    suspend fun getRideHistory(
        @Query("driver_id") driverId: String
    ): HistoryResponse

    @FormUrlEncoded
    @POST("add_refuel.php")
    fun addRefuel(
        @Field("fuel_amount") fuelAmount: Float,
        @Field("cost") cost: Float,
        @Field("odometer") odometer: Int
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("start_shift.php")
    fun startShift(
        @Field("vehicle_plate") rejestracja: String,
        @Field("start_odometer") przebieg: Int
    ): Call<StartShiftResponse>


    @FormUrlEncoded
    @POST("end_shift.php")
    fun endShift(
        @Field("end_odometer") odometer: Int
    ): Call<GenericResponse>

    @FormUrlEncoded
    @POST("update_fcm_token.php")
    fun updateFcmToken(
        @Field("fcm_token") fcmToken: String
    ): Call<GenericResponse>

    @Multipart
    @POST("add_service.php")
    fun addService(
        @Part("opis") opis: RequestBody,
        @Part("koszt") koszt: RequestBody,
        @Part("rejestracja") rejestracja: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): Call<GenericResponse>

    @GET("get_service.php")
    fun getServices(
        @Query("rejestracja") rejestracja: String
    ): Call<ServicesResponse>

    @GET("get_service_detail.php")
    fun getServiceDetail(
        @Query("id") id: Int
    ): Call<ServiceDetailResponse>

    @Multipart
    @POST("add_damage.php")
    fun addDamage(
        @Part("rejestracja") rejestracja: RequestBody,
        @Part("nr_szkody") nrSzkody: RequestBody,
        @Part("opis") opis: RequestBody,
        @Part("status") status: RequestBody,
        @Part photos: List<MultipartBody.Part>,
    ): Call<GenericResponse>

    @GET("get_damages.php")
    fun getDamages(
        @Query("rejestracja") rejestracja: String
    ): Call<DamagesResponse>

    @Multipart
    @POST("update_service.php")
    fun updateService(
        @Part("id") id: RequestBody,
        @Part("opis") opis: RequestBody,
        @Part("koszt") koszt: RequestBody,
        @Part photos: List<MultipartBody.Part>,
    ): Call<GenericResponse>

    @Multipart
    @POST("update_damage.php")
    fun updateDamage(
        @Part("id") id: RequestBody,
        @Part("rejestracja") rejestracja: RequestBody,
        @Part("nr_szkody") nrSzkody: RequestBody,
        @Part("opis") opis: RequestBody,
        @Part("status") status: RequestBody,
        @Part photos: List<MultipartBody.Part>,
    ): Call<GenericResponse>
}

package com.partner.taxi

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import com.partner.taxi.BuildConfig
import java.io.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://164.126.143.20:8444/api/"

    // tutaj będziemy przechowywać token i identyfikator urządzenia po zalogowaniu
    var jwtToken: String? = null
    var deviceId: String? = null
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Interceptor dokładający Authorization, jeśli token != null
    private val authInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder().apply {
            jwtToken?.let {
                addHeader("Authorization", "Bearer $it")
            }
            deviceId?.let {
                addHeader("Device-Id", it)
            }
        }.build()
        chain.proceed(req)
    }

    // Interceptor reagujący na błędy 401 i próbujący odświeżyć token
    private val unauthorizedInterceptor = Interceptor { chain ->
        var request = chain.request()
        var response = chain.proceed(request)
        if (response.code == 401) {
            when (val refreshResult = refreshToken()) {
                RefreshTokenResult.Success -> {
                    response.close()
                    request = request.newBuilder().apply {
                        jwtToken?.let { addHeader("Authorization", "Bearer $it") }
                        deviceId?.let { addHeader("Device-Id", it) }
                    }.build()
                    response = chain.proceed(request)
                }

                RefreshTokenResult.Unauthorized -> {
                    Log.w("ApiClient", "Token refresh rejected by server. Clearing local session.")
                    val device = deviceId ?: "unknown"
                    RemoteLogService.logWarning(
                        summary = "Odrzucono odświeżenie tokenu",
                        details = "Serwer odrzucił prośbę o odświeżenie dla urządzenia $device"
                    )
                    appContext?.let { ctx ->
                        jwtToken = null
                        SessionManager.clearToken(ctx)
                    }
                }

                is RefreshTokenResult.NetworkError -> {
                    response.close()
                    RemoteLogService.logHandledException(
                        summary = "Błąd sieci podczas odświeżania tokenu",
                        throwable = refreshResult.exception
                    )
                    throw TokenRefreshException(
                        "Nie udało się odświeżyć tokenu z powodu problemu z siecią.",
                        refreshResult.exception
                    )
                }

            }
        }
        response
    }

    // HttpLoggingInterceptor for debugging network calls.
    // Logging is enabled only for debug builds. Adjust the level or disable
    // it entirely for production by changing the condition below.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    // OkHttpClient z interceptor’em
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(unauthorizedInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    // Osobny klient bez unauthorizedInterceptor do odświeżania tokenu
    private val refreshClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit łączący się przez nasz OkHttpClient
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val refreshRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(refreshClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun refreshToken(): RefreshTokenResult {
        val device = deviceId ?: return RefreshTokenResult.Unauthorized
        val service = refreshRetrofit.create(ApiService::class.java)
        return try {
            val call = service.refreshToken(device)
            val resp = call.execute()
            if (resp.isSuccessful) {
                val newToken = resp.body()?.token
                if (!newToken.isNullOrEmpty()) {
                    jwtToken = newToken
                    appContext?.let { SessionManager.saveToken(it, newToken) }
                    RefreshTokenResult.Success
                } else {
                    Log.w("ApiClient", "Refresh token response missing token field")
                    RefreshTokenResult.Unauthorized
                }

            } else if (resp.code() == 401 || resp.code() == 403 || resp.code() == 404) {
                val code = resp.code()
                val summary = if (code == 404) {
                    "Endpoint odświeżania tokenu niedostępny"
                } else {
                    "Odrzucono odświeżenie tokenu"
                }
                RemoteLogService.logWarning(
                    summary = summary,
                    details = "HTTP $code podczas odświeżania tokenu"
                )
                RefreshTokenResult.Unauthorized
            } else {
                Log.w(
                    "ApiClient",
                    "Unexpected response while refreshing token: ${resp.code()} ${resp.message()}"
                )
                RemoteLogService.logWarning(
                    summary = "Nieoczekiwana odpowiedź odświeżenia tokenu",
                    details = "Kod ${resp.code()} ${resp.message()}"
                )
                RefreshTokenResult.NetworkError(
                    IOException("Refresh token request failed with HTTP ${resp.code()}")
                )
            }
        } catch (io: IOException) {
            RemoteLogService.logHandledException(
                summary = "Wyjątek sieci podczas odświeżania tokenu",
                throwable = io
            )
            RefreshTokenResult.NetworkError(io)
        } catch (e: Exception) {
            Log.e("ApiClient", "Unexpected error while refreshing token", e)
            RemoteLogService.logHandledException(
                summary = "Nieoczekiwany błąd podczas odświeżania tokenu",
                throwable = e
            )
            RefreshTokenResult.NetworkError(
                IOException(e.message ?: "Unexpected error during token refresh", e)
            )
        }
    }

    private sealed class RefreshTokenResult {
        object Success : RefreshTokenResult()
        object Unauthorized : RefreshTokenResult()
        data class NetworkError(val exception: IOException) : RefreshTokenResult()
    }

    suspend fun updateSaldo(request: UpdateSaldoRequest): retrofit2.Response<UpdateSaldoResponse> {
        return apiService.updateSaldo(request)
    }

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

package com.partner.taxi

import android.content.Context
import android.content.Intent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import com.partner.taxi.BuildConfig
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

    // Interceptor reagujący na błędy 401 i wylogowujący użytkownika
    private val unauthorizedInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            appContext?.let { ctx ->
                SessionManager.clearSession(ctx)
                jwtToken = null
                deviceId = null
                val intent = Intent(ctx, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                ctx.startActivity(intent)
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

    // Retrofit łączący się przez nasz OkHttpClient
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

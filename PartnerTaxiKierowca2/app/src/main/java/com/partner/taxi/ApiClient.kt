package com.partner.taxi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import com.partner.taxi.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://164.126.143.20:8444/api/"

    // tutaj będziemy przechowywać token po zalogowaniu
    var jwtToken: String? = null

    // Interceptor dokładający Authorization, jeśli token != null
    private val authInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder().apply {
            jwtToken?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()
        chain.proceed(req)
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

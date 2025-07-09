package com.partner.taxi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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

    // OkHttpClient z interceptor’em
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Retrofit łączący się przez nasz OkHttpClient
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

package com.example.pasteleriamilsabores.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 es la dirección especial del emulador para acceder al localhost del PC (Felipe)
    // Asegurar de que el puerto sea el mismo que se configuró en Spring Boot (8081)
    const val BASE_URL = "http://98.82.114.243:8081/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
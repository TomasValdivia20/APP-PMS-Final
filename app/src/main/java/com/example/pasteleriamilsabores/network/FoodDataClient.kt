package com.example.pasteleriamilsabores.network

import com.example.pasteleriamilsabores.Model.FdcSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Interfaz de la API
interface FoodDataApiService {
    @GET("foods/search")
    suspend fun searchFoods(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 5 // Limitamos a 5 resultados para no saturar
    ): FdcSearchResponse
}

// 2. Cliente Retrofit Específico
object FoodDataClient {
    private const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/"

    // 🛑 ¡REEMPLAZA ESTO CON TU KEY REAL!
    const val API_KEY = "Qlw276R4VAAtlnt9hYqOgpc0PizhmtV74RohpYCq"

    val service: FoodDataApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodDataApiService::class.java)
    }
}
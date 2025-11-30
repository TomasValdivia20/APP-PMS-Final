package com.example.pasteleriamilsabores.network

import com.example.pasteleriamilsabores.Model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- CATEGORÍAS ---
    @GET("api/categorias")
    suspend fun obtenerCategorias(): List<Categoria>

    @POST("api/categorias")
    suspend fun crearCategoria(@Body categoria: Categoria): Categoria

    @PUT("api/categorias/{id}")
    suspend fun actualizarCategoria(@Path("id") id: Int, @Body categoria: Categoria): Categoria

    @DELETE("api/categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Int): Response<Void>

    // --- PRODUCTOS ---
    @GET("api/productos")
    suspend fun obtenerProductos(): List<Producto>

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: Producto): Producto

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Int, @Body producto: Producto): Producto

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Int): Response<Void>

    // --- ARCHIVOS ---
    @Multipart
    @POST("api/files/upload")
    suspend fun subirImagen(@Part file: MultipartBody.Part): Map<String, String>

    // --- ORDENES Y STOCK ---
    @POST("api/ordenes")
    suspend fun crearOrden(@Body request: OrdenRequest): Response<Any>


    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioBackoffice>

    @GET("api/ordenes")
    suspend fun obtenerOrdenes(): List<OrdenResponse> // Antes era List<Map<...>>

    @GET("api/reportes/ventas")
    suspend fun obtenerReporteVentas(): ReporteVentas // Antes era Map<String, Int>
}
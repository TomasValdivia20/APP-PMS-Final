package com.example.pasteleriamilsabores.repository

import android.content.Context
import com.example.pasteleriamilsabores.Model.Producto
import com.example.pasteleriamilsabores.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductoRepository {

    suspend fun obtenerTodosLosProductos(context: Context): List<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.obtenerProductos()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun obtenerProductoPorId(context: Context, id: Int): Producto? {
        // (Misma implementación de filtrado en memoria por ahora)
        return withContext(Dispatchers.IO) {
            try {
                val todos = RetrofitClient.instance.obtenerProductos()
                todos.find { it.id == id }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun crearProducto(producto: Producto): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.crearProducto(producto)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun eliminarProducto(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.eliminarProducto(id)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
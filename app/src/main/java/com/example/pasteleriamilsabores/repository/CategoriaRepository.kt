package com.example.pasteleriamilsabores.repository

import android.content.Context
import com.example.pasteleriamilsabores.Model.Categoria
import com.example.pasteleriamilsabores.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoriaRepository {

    suspend fun obtenerCategorias(context: Context): List<Categoria> {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.obtenerCategorias()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun crearCategoria(categoria: Categoria): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.crearCategoria(categoria)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun eliminarCategoria(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.instance.eliminarCategoria(id)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Implementar obtenerCategoriasDesdeAssets para compatibilidad si es necesario
    fun obtenerCategoriasDesdeAssets(context: Context): List<Categoria> = emptyList()
}
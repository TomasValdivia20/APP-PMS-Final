package com.example.pasteleriamilsabores.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.Categoria
import com.example.pasteleriamilsabores.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaViewModel (
    private val repo: CategoriaRepository = CategoriaRepository()
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun cargarCategorias(context: Context) {
        viewModelScope.launch {
            _loading.value = true
            // 🛑 CAMBIO CLAVE: Llamamos a 'obtenerCategorias' (la nueva función con Retrofit)
            // en lugar de 'obtenerCategoriasDesdeAssets'
            val list = repo.obtenerCategorias(context)
            _categorias.value = list
            _loading.value = false
        }
    }

    // Función auxiliar para buscar por ID (si se usa en la UI)
    fun buscarCategoriaPorId(id: Int): Categoria? =
        _categorias.value.find { it.id == id }
}
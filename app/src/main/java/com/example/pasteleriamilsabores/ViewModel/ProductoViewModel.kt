package com.example.pasteleriamilsabores.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.Producto
import com.example.pasteleriamilsabores.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 1. EL VIEWMODEL RECIBE EL ID Y EL REPOSITORIO
class ProductoViewModel (
    private val context: Context,
    private val categoriaId: Int, // ID que viene desde HomeScreen
    private val repo: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Se carga automáticamente al crearse
    init {
        cargarProductosFiltrados()
    }

    private fun cargarProductosFiltrados() {
        viewModelScope.launch {
            _loading.value = true

            // Cargar todos los productos desde la API (Repositorio actualizado)
            val allProductos = repo.obtenerTodosLosProductos(context)

            // 2. LÓGICA DE FILTRADO
            // IMPORTANTE: Ahora 'categoria' es un objeto, no un ID directo.
            // Accedemos a 'categoria.id' para filtrar.
            val filteredList = allProductos.filter { it.categoria?.id?.toLong() == categoriaId.toLong() } // Convertimos a Long si es necesario por el Backend

            _productos.value = filteredList
            _loading.value = false
        }
    }
}

// 3. LA FACTORY (OBLIGATORIA) PARA PASAR ARGUMENTOS AL CONSTRUCTOR
class ProductoViewModelFactory(
    private val context: Context,
    private val categoriaId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(context, categoriaId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
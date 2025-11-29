package com.example.pasteleriamilsabores.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.Producto
import com.example.pasteleriamilsabores.Model.VarianteProducto // Nuevo Modelo
import com.example.pasteleriamilsabores.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleProductoViewModel (
    private val context: Context,
    private val productoId: Int,
    private val repo: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto

    // Actualizado a VarianteProducto
    private val _varianteSeleccionada = MutableStateFlow<VarianteProducto?>(null)
    val varianteSeleccionada: StateFlow<VarianteProducto?> = _varianteSeleccionada

    init {
        loadProducto()
    }

    private fun loadProducto() {
        viewModelScope.launch {
            val loadedProducto = repo.obtenerProductoPorId(context, productoId)
            _producto.value = loadedProducto

            // Usamos 'variantes' en vez de 'tamaños'
            if (loadedProducto != null && loadedProducto.variantes.isNotEmpty()) {
                _varianteSeleccionada.value = loadedProducto.variantes.first()
            }
        }
    }

    fun seleccionarVariante(variante: VarianteProducto) {
        _varianteSeleccionada.value = variante
    }
}

// Factory sin cambios
class DetalleProductoViewModelFactory(
    private val context: Context,
    private val productoId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleProductoViewModel::class.java)) {
            return DetalleProductoViewModel(context, productoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.pasteleriamilsabores.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    // Lista de ítems en el carrito
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    // Estado del descuento
    private val _descuentoCodigo = MutableStateFlow<String?>(null) // Código aplicado
    val descuentoCodigo: StateFlow<String?> = _descuentoCodigo

    private val _descuentoPorcentaje = MutableStateFlow(0.0)
    val descuentoPorcentaje: StateFlow<Double> = _descuentoPorcentaje

    private val VALID_DISCOUNT_CODE = "PMS50AGNOS"
    private val DISCOUNT_VALUE = 0.10 // 10%

    // --- CÁLCULOS DEL CARRO ---

    // Calcula el subtotal total (suma de todos los ítems sin descuento)
    val subtotalTotal: StateFlow<Int> = _items.map { items ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    // Calcula el descuento total
    val descuentoTotal: StateFlow<Int> = subtotalTotal.combine(_descuentoPorcentaje) { subtotal, descuento ->
        (subtotal * descuento).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    // Calcula el total final a pagar
    val totalPagar: StateFlow<Int> = subtotalTotal.combine(descuentoTotal) { subtotal, descuento ->
        subtotal - descuento
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)


    // --- LÓGICA DE ITEMS ---

    fun addItem(item: CartItem) {
        _items.update { currentItems ->
            // En una app real, podrías querer sumar cantidades si el producto y variante son iguales.
            currentItems + item
        }
    }

    fun removeItem(itemId: String) {
        _items.update { currentItems ->
            currentItems.filter { it.id != itemId }
        }
    }

    fun limpiarCarrito() {
        _items.value = emptyList()
        _descuentoCodigo.value = null
        _descuentoPorcentaje.value = 0.0
    }

    // --- LÓGICA DE DESCUENTO ---

    // Retorna el mensaje de validación del código
    fun validarCodigo(code: String): String {
        return when (code.uppercase()) {
            VALID_DISCOUNT_CODE -> {
                _descuentoCodigo.value = VALID_DISCOUNT_CODE
                _descuentoPorcentaje.value = DISCOUNT_VALUE
                "Código '$code' aplicado exitosamente (10% de descuento)!"
            }
            "EXPIRADO" -> { // Simulación
                _descuentoCodigo.value = null
                _descuentoPorcentaje.value = 0.0
                "El código ha expirado."
            }
            else -> {
                _descuentoCodigo.value = null
                _descuentoPorcentaje.value = 0.0
                "Código '$code' no reconocido."
            }
        }
    }
}
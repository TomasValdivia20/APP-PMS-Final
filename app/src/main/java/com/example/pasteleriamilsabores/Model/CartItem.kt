package com.example.pasteleriamilsabores.Model

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val productoId: Int,
    val nombreProducto: String,
    val imagenProducto: String,
    // Actualizado a VarianteProducto
    val varianteSeleccionada: VarianteProducto,
    val cantidad: Int = 1
) {
    val subtotal: Int
        get() = varianteSeleccionada.precio * cantidad
}
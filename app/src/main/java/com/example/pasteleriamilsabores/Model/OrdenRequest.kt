package com.example.pasteleriamilsabores.Model

data class OrdenRequest(
    val usuarioId: Long,
    val total: Int,
    val detalles: List<DetalleRequest>
)

data class DetalleRequest(
    val productoId: Int,
    val varianteId: Int,
    val cantidad: Int,
    val precioUnitario: Int
)
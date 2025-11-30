package com.example.pasteleriamilsabores.Model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val imagen: String,
    val precioBase: Int,
    // El backend envía el objeto categoría completo, no solo el ID
    val categoria: Categoria?,
    // Cambiamos 'tamaños' por 'variantes' para coincidir con el Backend
    val variantes: List<VarianteProducto> = emptyList(),
    val notas: String? = ""
)
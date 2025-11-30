package com.example.pasteleriamilsabores.Model

import com.google.gson.annotations.SerializedName

data class VarianteProducto(
    val id: Int,
    val nombre: String, // Ej: "12 personas"
    val precio: Int,
    val stock: Int,
    @SerializedName("infoNutricional") val infoNutricional: String // Ahora es un texto simple
)
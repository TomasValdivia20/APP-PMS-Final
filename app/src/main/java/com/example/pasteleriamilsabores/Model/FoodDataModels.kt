package com.example.pasteleriamilsabores.Model

import com.google.gson.annotations.SerializedName

// Respuesta principal de la búsqueda
data class FdcSearchResponse(
    val foods: List<FdcFood>
)

// Detalle de cada alimento
data class FdcFood(
    val fdcId: Int,
    val description: String,
    val brandOwner: String?,
    val foodNutrients: List<FdcNutrient>?
)

// Nutrientes
data class FdcNutrient(
    val nutrientName: String,
    val value: Double,
    val unitName: String
)
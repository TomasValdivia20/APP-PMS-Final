package com.example.pasteleriamilsabores.Model

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val direccion: String,
    val rut: String,
    val region: String,
    val comuna: String
)
package com.example.pasteleriamilsabores.Model

data class OrdenResponse(
    val id: Long,
    val fecha: String, // Spring Boot envía fecha como String ISO
    val total: Int,
    val estado: String,
    val usuario: UsuarioBackoffice // La orden trae los datos del cliente
)
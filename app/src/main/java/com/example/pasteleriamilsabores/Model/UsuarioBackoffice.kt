package com.example.pasteleriamilsabores.Model

// Modelo actualizado para coincidir con la respuesta del Backend
data class UsuarioBackoffice(
    val id: Long,          // Antes era String, ahora es Long (ID de BBDD)
    val rut: String?,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: Rol           // 🛑 CAMBIO CLAVE: Ahora es un objeto Rol, no un String
)

// Clase anidada o separada para el Rol
data class Rol(
    val id: Long,
    val nombre: String     // Aquí está la propiedad .nombre que busca tu código
)
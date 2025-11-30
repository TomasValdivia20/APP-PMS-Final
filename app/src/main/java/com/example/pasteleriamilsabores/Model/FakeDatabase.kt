package com.example.pasteleriamilsabores.Model

object FakeDatabase {
    val usuarios: MutableList<Usuario> = mutableListOf(
        Usuario(
            id = 1,
            nombre = "Juan Carlos",
            apellido = "Bodoque",
            email = "juan@gmail.com",
            password = "123",
            direccion = "Calle Falsa 123",
            rut = "12345678-9",
            region = "Metropolitana",
            comuna = "Santiago"
        ),
        Usuario(
            id = 2,
            nombre = "Admin",
            apellido = "User",
            email = "admin@milsabores.cl",
            password = "admin",
            direccion = "Oficina Central 1",
            rut = "98765432-1",
            region = "Metropolitana",
            comuna = "Providencia"
        )
    )
}
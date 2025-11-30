package com.example.pasteleriamilsabores.Utils

import com.example.pasteleriamilsabores.network.RetrofitClient

// --- Validar correo, password y rut ---
fun validarCampos(
    nombre: String,
    apellido: String,
    rut: String,
    region: String,
    comuna: String,
    direccion: String,
    email: String,
    password: String
): String? {
    // Validar nombre
    if (nombre.trim().isEmpty()) {
        return "El nombre no puede estar vacío"
    }

    // Validar apellido
    if (apellido.trim().isEmpty()) {
        return "El apellido no puede estar vacío"
    }

    // Validar RUT chileno
    if (!validarRut(rut)) {
        return "RUT inválido"
    }

    // Validar Region seleccionada
    if (region.trim().isEmpty()) {
        return "Debes seleccionar una región"
    }

    // Validar Comuna seleccionada
    if (comuna.trim().isEmpty()) {
        return "Debes seleccionar una comuna"
    }

    // Validar dirección
    if (direccion.trim().isEmpty()) {
        return "La dirección no puede estar vacía"
    }

    // Validar correo electrónico
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
    if (!emailRegex.matches(email)) {
        return "Correo inválido"
    }

    // Validar contraseña
    if (password.length < 6) {
        return "La contraseña debe tener al menos 6 caracteres"
    }



    return null // Todo OK
}


// --- Función para validar RUT chileno ---
fun validarRut(input: String): Boolean {
    val rutLimpio = input.lowercase()
        .replace(Regex("[^0-9k-]"), "") // solo números, guion o k
        .trim()

    if (!Regex("^[0-9]{1,8}-[0-9kK]$").matches(rutLimpio)) return false

    val cuerpo = rutLimpio.substringBefore("-")
    val dvIngresado = rutLimpio.substringAfter("-").first()

    var suma = 0
    var multiplo = 2

    for (i in cuerpo.reversed()) {
        suma += Character.getNumericValue(i) * multiplo
        multiplo = if (multiplo == 7) 2 else multiplo + 1
    }

    val resto = suma % 11
    val dvCalculadoInt = 11 - resto
    val dvEsperado = when (dvCalculadoInt) {
        11 -> '0'
        10 -> 'k'
        else -> dvCalculadoInt.toString().first()
    }

    return dvIngresado == dvEsperado
}

// Funcion helper para imagenes
fun construirUrlImagen(imagen: String?): String {
    if (imagen.isNullOrEmpty()) return "https://placehold.co/400?text=Sin+Imagen"

    return when {
        // Caso 1: Ya es una URL completa (ej: placeholder web)
        imagen.startsWith("http") -> imagen

        // Caso 2: Es una imagen nueva subida al Backend (empieza con 'uploads/')
        imagen.startsWith("uploads/") -> "${RetrofitClient.BASE_URL}$imagen"

        // Caso 3: Es una imagen antigua de Productos (venía como "/assets/img/foto.jpg")
        // La limpiamos para que apunte a los assets de Android correctamente.
        imagen.contains("assets/img/") -> {
            val nombreArchivo = imagen.substringAfterLast("/")
            "file:///android_asset/img/$nombreArchivo"
        }

        // Caso 4: Es una imagen antigua de Categorías (venía solo como "foto.jpg")
        // Asumimos que es un asset local si no tiene barras '/'
        !imagen.contains("/") -> "file:///android_asset/img/$imagen"

        // Caso por defecto: Asumimos que es del backend
        else -> "${RetrofitClient.BASE_URL}$imagen"
    }
}
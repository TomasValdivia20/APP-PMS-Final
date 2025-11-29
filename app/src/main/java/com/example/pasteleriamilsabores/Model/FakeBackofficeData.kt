package com.example.pasteleriamilsabores.Model

// Datos Ficticios para la simulación de ventas y gráficos
object FakeBackofficeData {

    val ventasRecientes = listOf(
        Venta("Sebastián", "Carrasco", "Recoleta 140", "Metropolitana", "2025/04/25", 16800),
        Venta("Javier", "Cornejo", "O'Higgins 2", "Metropolitana", "2025/07/25", 170750),
        Venta("Ricardo", "Piscina", "Perú 12", "Arica", "2025/01/12", 86000),
        Venta("Alfonso", "Sandoval", "Valparaíso 12", "Valparaíso", "2025/03/29", 433060),
        Venta("Kike", "Morandé", "Viña del Mar 67", "Valparaíso", "2025/11/28", 162700),
        Venta("Willy", "Sabor", "Monjitas 54", "Metropolitana", "2025/12/02", 372000),
        Venta("Anita", "Alvarado", "Jorge Pratt 76", "Metropolitana", "2025/08/06", 137500),
        Venta("Pancho", "Delsur", "Bombero Nuñez 34", "Metropolitana", "2025/10/14", 327900),
        Venta("Pedro", "Pascal", "Monjitas 52", "Metropolitana", "2025/09/15", 205500),
        Venta("Sergio", "Lagos", "La Moneda 3", "Metropolitana", "2025/12/13", 103600)
    )

    // Datos ficticios para el gráfico (Ventas de los últimos 15 días)
    // Usaremos valores simples para simular los puntos.
    val ventas15Dias: List<Pair<String, Float>> = listOf(
        "Día 1" to 500f, "Día 2" to 750f, "Día 3" to 600f, "Día 4" to 900f, "Día 5" to 1200f,
        "Día 6" to 1100f, "Día 7" to 1500f, "Día 8" to 1300f, "Día 9" to 1450f, "Día 10" to 1600f,
        "Día 11" to 1800f, "Día 12" to 1700f, "Día 13" to 2000f, "Día 14" to 2100f, "Día 15" to 2500f
    )

    // Datos ficticios para el gráfico (Ventas Semestre)
    val ventasSemestre: List<Pair<String, Float>> = listOf(
        "Ene" to 8000f, "Feb" to 9500f, "Mar" to 12000f, "Abr" to 10500f, "May" to 15000f, "Jun" to 18000f
    )

    val usuarioActual = UsuarioBackoffice(
        id = "e4e2a1b3",
        nombre = "Admin",
        apellido = "Mil Sabores",
        correo = "admin@milsabores.cl",
        rol = "Administrador"
    )

    val usuariosFicticios = listOf(
        UsuarioBackoffice("u1", "Juan", "Pérez", "juan.perez@cliente.cl", "Cliente"),
        UsuarioBackoffice("u2", "María", "González", "maria.gonzalez@cliente.cl", "Cliente"),
        UsuarioBackoffice("e1", "Pedro", "Rojas", "pedro.rojas@empleado.cl", "Empleado"),
        usuarioActual // Incluimos al admin actual en la lista
    )
}
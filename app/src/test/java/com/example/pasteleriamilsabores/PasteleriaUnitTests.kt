package com.example.pasteleriamilsabores

import com.example.pasteleriamilsabores.Model.CartItem
import com.example.pasteleriamilsabores.Model.VarianteProducto
import com.example.pasteleriamilsabores.Utils.validarCampos
import com.example.pasteleriamilsabores.Utils.validarRut
import org.junit.Test
import org.junit.Assert.*

class PasteleriaUnitTests {

    // --- PRUEBAS DE LÓGICA DE NEGOCIO (MODELO) ---

    @Test
    fun testCalculoSubtotalCarrito() {
        // 1. Verificar que el subtotal se calcula: precio * cantidad
        val variante = VarianteProducto(1, "12p", 10000, 5, "Info")
        val item = CartItem(
            productoId = 1,
            nombreProducto = "Torta",
            imagenProducto = "img",
            varianteSeleccionada = variante,
            cantidad = 2
        )
        assertEquals(20000, item.subtotal)
    }

    @Test
    fun testCalculoSubtotalCantidadUno() {
        // 2. Verificar subtotal con cantidad 1
        val variante = VarianteProducto(1, "12p", 15000, 5, "Info")
        val item = CartItem(
            productoId = 1,
            nombreProducto = "Torta",
            imagenProducto = "img",
            varianteSeleccionada = variante,
            cantidad = 1
        )
        assertEquals(15000, item.subtotal)
    }

    // --- PRUEBAS DE VALIDACIÓN DE RUT (UTILS) ---

    @Test
    fun testValidarRutCorrecto() {
        // 3. RUT válido real (12.345.678-5 es matemáticamente correcto)
        assertTrue(validarRut("12.345.678-5".replace(".", "")))
        // Otro ejemplo válido: 30.686.957-4
        assertTrue(validarRut("30.686.957-4"))
    }

    @Test
    fun testValidarRutIncorrectoDigito() {
        // 4. RUT con dígito verificador erróneo (El DV de 12.345.678 es 5, no 9)
        assertFalse(validarRut("12.345.678-9"))
    }

    @Test
    fun testValidarRutFormatoInvalido() {
        // 5. RUT con caracteres no válidos o formato roto
        assertFalse(validarRut("12.345.678")) // Falta guion
        assertFalse(validarRut("rut-invalido"))
    }

    // --- PRUEBAS DE VALIDACIÓN DE FORMULARIO (UTILS) ---

    @Test
    fun testRegistroCamposValidos() {
        // 6. Todos los campos correctos (Usando RUT válido 12.345.678-5)
        val error = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            rut = "12345678-5",
            region = "Metropolitana",
            comuna = "Santiago",
            direccion = "Calle 123",
            email = "juan@gmail.com",
            password = "password123"
        )
        assertNull(error)
    }

    @Test
    fun testRegistroNombreVacio() {
        // 7. Nombre vacío debe devolver error específico
        val error = validarCampos(
            nombre = "",
            apellido = "Pérez",
            rut = "12345678-5",
            region = "RM", comuna = "Stgo", direccion = "Calle 1", email = "a@a.com", password = "123"
        )
        assertEquals("El nombre no puede estar vacío", error)
    }

    @Test
    fun testRegistroEmailInvalido() {
        // 8. Email sin formato correcto
        val error = validarCampos(
            nombre = "Juan", apellido = "Pérez", rut = "12345678-5",
            region = "RM", comuna = "Stgo", direccion = "Calle 1",
            email = "correomalo", // Sin @ ni dominio
            password = "password123"
        )
        assertEquals("Correo inválido", error)
    }

    @Test
    fun testRegistroPasswordCorta() {
        // 9. Contraseña muy corta
        val error = validarCampos(
            nombre = "Juan", apellido = "Pérez", rut = "12345678-5",
            region = "RM", comuna = "Stgo", direccion = "Calle 1", email = "a@a.com",
            password = "123" // Menos de 6 caracteres
        )
        assertEquals("La contraseña debe tener al menos 6 caracteres", error)
    }

    @Test
    fun testRegistroFaltaComuna() {
        // 10. Falta seleccionar comuna
        val error = validarCampos(
            nombre = "Juan", apellido = "Pérez", rut = "12345678-5",
            region = "Metropolitana",
            comuna = "", // Vacío
            direccion = "Calle 1", email = "a@a.com", password = "password123"
        )
        assertEquals("Debes seleccionar una comuna", error)
    }
}
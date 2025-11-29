
package com.example.pasteleriamilsabores


object Destinos {
    // Rutas de inicio de sesión y registro (opcionales aquí, pero buenas para centralizar)
    const val LOGIN_SCREEN = "login"
    const val REGISTER_SCREEN = "register"

    // Rutas para Home y Categorías
    const val HOME_SCREEN = "home/{email}"

    // Rutas de Productos
    const val PRODUCTOS_SCREEN = "productos_por_categoria/{categoriaId}/{categoriaNombre}"

    // Rutas de Detalle de Producto
    const val DETALLE_PRODUCTO_SCREEN = "detalle_producto_screen/{productoId}"

    const val CART_SCREEN = "cart_screen" //Contante para carritooo

    const val COMPRA_FINALIZADA_SCREEN = "compra_finalizada_screen"

    const val COMPRA_RECHAZADA_SCREEN = "compra_rechazada_screen"

    // RUTAS DEL BACKOFFICE (ADMIN) -
    const val BACKOFFICE_BASE = "backoffice" // La ruta que contendrá el NavHost interno
    const val BODASHBOARD = "bo/dashboard"
    const val BOORDENES = "bo/ordenes"
    const val BOPRODUCTO = "bo/productos"
    const val BOCATEGORIA = "bo/categorias"
    const val BOUSUARIO = "bo/usuarios"
    const val BOREPORTES = "bo/reportes"
    const val BOPERFIL = "bo/perfil"

    const val FOOD_INFO_SCREEN = "food_info_screen"

    // Función "helper" para crear la ruta de detalle con el ID
    fun crearRutaDetalleProducto(productoId: Int) = "detalle_producto_screen/$productoId"

    fun crearRutaProducto(categoriaId: Int, categoriaNombre: String): String {
        return "productos_por_categoria/$categoriaId/$categoriaNombre"
    }
}
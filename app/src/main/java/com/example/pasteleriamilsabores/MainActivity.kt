package com.example.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.pasteleriamilsabores.ViewModel.AuthViewModel
import com.example.pasteleriamilsabores.View.*
import com.example.pasteleriamilsabores.Destinos


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pasteleriamilsabores.ViewModel.BOViewModel


// Opcional: Puedes definir tus rutas aquí para evitar "hardcode"



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = viewModel()

            //🛑 CLAVE: Definimos una variable que contenga la ruta completa del Home.
            // Esta ruta siempre debe ser la base para compartir el CartViewModel.
            val HOME_ROUTE = Destinos.HOME_SCREEN


            // Usamos las constantes de Destinos
            NavHost(navController, startDestination = Destinos.REGISTER_SCREEN) {
                composable(Destinos.REGISTER_SCREEN) {
                    RegisterScreen(navController, viewModel)
                }
                composable(Destinos.LOGIN_SCREEN) {
                    LoginScreen(navController, viewModel)
                }

                // HOME SCREEN
                composable(Destinos.HOME_SCREEN) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    // OBTENEMOS EL ALCANCE USANDO LA MISMA RUTA LITERAL COMPLETA
                    // 'home/{email}' está activo, por lo que podemos usarlo.
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    HomeScreen(
                        navController = navController,
                        email = email,
                        cartViewModel = viewModel(sharedViewModelStoreOwner) // Inyección
                    )
                }

                // PRODUCTO SCREEN
                composable(
                    route = Destinos.PRODUCTOS_SCREEN, // productos_por_categoria/{categoriaId}/{categoriaNombre}
                    arguments = listOf(
                        navArgument("categoriaId") { type = NavType.IntType },
                        navArgument("categoriaNombre") { type = NavType.StringType }
                    )
                ) { backStackEntry ->

                    // 1. OBTENCIÓN DE ARGUMENTOS CON NOMBRES TEMPORALES (Evitando conflicto)
                    val idArg = backStackEntry.arguments?.getInt("categoriaId") ?: 0
                    val nombreArg = backStackEntry.arguments?.getString("categoriaNombre") ?: "Categoría Desconocida"

                    // Se aplica el reemplazo de '+' en el nombre
                    val categoriaNombreLimpio = nombreArg.replace('+', ' ')

                    // 2. OBTENCIÓN DEL ALCANCE COMPARTIDO (Mismo que en HOME)
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    ProductoScreen(
                        categoriaId = idArg, // Usamos la variable temporal
                        categoriaNombre = categoriaNombreLimpio,
                        navController = navController
                        // ProductoScreen solo necesita CategoriaViewModel y ProductoViewModel,
                        // no necesita CartViewModel.
                    )
                }

                // -------------------------------------------------------------------


                // DETALLE PRODUCTO SCREEN
                composable(
                    route = Destinos.DETALLE_PRODUCTO_SCREEN, // detalle_producto_screen/{productoId}
                    arguments = listOf(
                        navArgument("productoId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->

                    // 1. OBTENCIÓN DEL ARGUMENTO CON NOMBRE TEMPORAL
                    val idProductoArg = backStackEntry.arguments?.getInt("productoId") ?: 0

                    // 2. OBTENCIÓN DEL ALCANCE COMPARTIDO
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    DetalleProductoScreen(
                        productoId = idProductoArg, // Usamos la variable temporal
                        navController = navController,
                        // 3. INYECCIÓN DEL CART VIEWMODEL
                        cartViewModel = viewModel(homeScreenEntry)
                    )
                }

                // CART SCREEN
                composable(Destinos.CART_SCREEN) { backStackEntry ->

                    // 4. OBTENEMOS EL ALCANCE SIEMPRE DEL HOME
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    CartScreen(
                        navController = navController,
                        cartViewModel = viewModel(sharedViewModelStoreOwner)
                    )
                }

                // PANTALLA DE COMPRA FINALIZADA
                composable(Destinos.COMPRA_FINALIZADA_SCREEN) { backStackEntry ->
                    // Obtenemos el alcance compartido (igual que para CartScreen)
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    CompraFinalizadaScreen(
                        navController = navController,
                        cartViewModel = viewModel(homeScreenEntry)
                    )
                }

                // PANTALLA DE COMPRA RECHAZADA
                composable(Destinos.COMPRA_RECHAZADA_SCREEN) { backStackEntry ->
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    CompraRechazadaScreen(
                        navController = navController

                    )
                }

                // RUTA BASE DEL BACKOFFICE
                composable(Destinos.BACKOFFICE_BASE) {
                    // El BO NavGraph es el NavHost anidado y obtiene su ViewModel internamente
                    BOBackofficeNavGraph(
                        navController = navController, // NavController principal (para cerrar sesión)

                    )
                }

                // RUTA DE INFO NUTRICIONAL (API EXTERNA)
                composable(Destinos.FOOD_INFO_SCREEN) {
                    FoodInfoScreen(navController = navController)
                }
            }
        }
    }
}
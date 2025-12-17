package com.example.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pasteleriamilsabores.View.*
import com.example.pasteleriamilsabores.ViewModel.AuthViewModel
// 🛑 ESTA IMPORTACIÓN ES CRUCIAL PARA QUE FUNCIONE 'Destinos'
import com.example.pasteleriamilsabores.Destinos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            // ViewModel de Autenticación (Se crea aquí para compartirlo)
            val authViewModel: AuthViewModel = viewModel()

            // Ruta base para compartir el CartViewModel
            val HOME_ROUTE = Destinos.HOME_SCREEN

            NavHost(navController, startDestination = Destinos.REGISTER_SCREEN) {

                // 1. REGISTRO
                composable(Destinos.REGISTER_SCREEN) {
                    RegisterScreen(navController, authViewModel)
                }

                // 2. LOGIN
                composable(Destinos.LOGIN_SCREEN) {
                    LoginScreen(navController, authViewModel)
                }

                // 3. HOME (TIENDA)
                composable(Destinos.HOME_SCREEN) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    // Scope compartido para el Carrito
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }
                    HomeScreen(
                        navController = navController,
                        email = email,
                        cartViewModel = viewModel(sharedViewModelStoreOwner)
                    )
                }

                // 4. LISTA DE PRODUCTOS (POR CATEGORÍA)
                composable(
                    route = Destinos.PRODUCTOS_SCREEN,
                    arguments = listOf(
                        navArgument("categoriaId") { type = NavType.IntType },
                        navArgument("categoriaNombre") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val idArg = backStackEntry.arguments?.getInt("categoriaId") ?: 0
                    val nombreArg = backStackEntry.arguments?.getString("categoriaNombre") ?: ""

                    ProductoScreen(
                        categoriaId = idArg,
                        categoriaNombre = nombreArg.replace('+', ' '),
                        navController = navController
                    )
                }

                // 5. DETALLE DE PRODUCTO
                composable(
                    route = Destinos.DETALLE_PRODUCTO_SCREEN,
                    arguments = listOf(navArgument("productoId") { type = NavType.IntType })
                ) { backStackEntry ->
                    // Recuperamos el scope compartido para usar el MISMO carrito
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    DetalleProductoScreen(
                        productoId = backStackEntry.arguments?.getInt("productoId") ?: 0,
                        navController = navController,
                        cartViewModel = viewModel(homeScreenEntry)
                    )
                }

                // 6. CARRITO DE COMPRAS
                composable(Destinos.CART_SCREEN) { backStackEntry ->
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    CartScreen(
                        navController = navController,
                        cartViewModel = viewModel(sharedViewModelStoreOwner),
                        authViewModel = authViewModel // Pasamos el usuario logueado
                    )
                }

                // 7. COMPRA FINALIZADA (ÉXITO)
                composable(Destinos.COMPRA_FINALIZADA_SCREEN) { backStackEntry ->
                    val homeScreenEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }
                    CompraFinalizadaScreen(
                        navController = navController,
                        cartViewModel = viewModel(homeScreenEntry)
                    )
                }

                // 8. COMPRA RECHAZADA (ERROR)
                composable(Destinos.COMPRA_RECHAZADA_SCREEN) {
                    CompraRechazadaScreen(navController = navController)
                }

                // 9. BACKOFFICE (ADMINISTRACIÓN)
                composable(Destinos.BACKOFFICE_BASE) {
                    // BOBackofficeNavGraph maneja su propio ViewModel internamente
                    BOBackofficeNavGraph(navController = navController)
                }

                // 10. INFORMACIÓN NUTRICIONAL (API EXTERNA)
                composable(
                    route = Destinos.FOOD_INFO_SCREEN,
                    arguments = listOf(navArgument("searchTerm") { type = NavType.StringType })
                ) { backStackEntry ->
                    val searchTerm = backStackEntry.arguments?.getString("searchTerm") ?: ""
                    FoodInfoScreen(navController = navController, initialQuery = searchTerm)
                }
            }
        }
    }
}
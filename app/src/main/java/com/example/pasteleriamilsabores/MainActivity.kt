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



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()

            //Definimos una variable que contenga la ruta completa del Home.
            // Esta ruta siempre debe ser la base para compartir el CartViewModel.
            val HOME_ROUTE = Destinos.HOME_SCREEN

            NavHost(navController, startDestination = Destinos.REGISTER_SCREEN) {
                composable(Destinos.REGISTER_SCREEN) {
                    RegisterScreen(navController, authViewModel)
                }
                composable(Destinos.LOGIN_SCREEN) {
                    LoginScreen(navController, authViewModel)
                }

                // HOME SCREEN
                composable(Destinos.HOME_SCREEN) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }
                    HomeScreen(
                        navController = navController,
                        email = email,
                        // Pasamos el authViewModel si HomeScreen lo necesitara (opcional),
                        // pero lo importante es pasarlo a CartScreen más abajo.
                        cartViewModel = viewModel(sharedViewModelStoreOwner)
                    )
                }

                // PRODUCTO SCREEN
                composable(
                    route = Destinos.PRODUCTOS_SCREEN,
                    arguments = listOf(navArgument("categoriaId") { type = NavType.IntType }, navArgument("categoriaNombre") { type = NavType.StringType })
                ) { backStackEntry ->
                    val idArg = backStackEntry.arguments?.getInt("categoriaId") ?: 0
                    val nombreArg = backStackEntry.arguments?.getString("categoriaNombre") ?: ""
                    ProductoScreen(categoriaId = idArg, categoriaNombre = nombreArg.replace('+', ' '), navController = navController)
                }

                // -------------------------------------------------------------------


                // DETALLE PRODUCTO SCREEN
                composable(
                    route = Destinos.DETALLE_PRODUCTO_SCREEN,
                    arguments = listOf(navArgument("productoId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val homeScreenEntry = remember(backStackEntry) { navController.getBackStackEntry(HOME_ROUTE) }
                    DetalleProductoScreen(productoId = backStackEntry.arguments?.getInt("productoId") ?: 0, navController = navController, cartViewModel = viewModel(homeScreenEntry))
                }

                // CART SCREEN
                composable(Destinos.CART_SCREEN) { backStackEntry ->
                    val sharedViewModelStoreOwner = remember(backStackEntry) {
                        navController.getBackStackEntry(HOME_ROUTE)
                    }

                    CartScreen(
                        navController = navController,
                        cartViewModel = viewModel(sharedViewModelStoreOwner),
                        authViewModel = authViewModel // PASAMOS LA INSTANCIA COMPARTIDA
                    )
                }

                // PANTALLA DE COMPRA FINALIZADA
                composable(Destinos.COMPRA_FINALIZADA_SCREEN) { backStackEntry ->
                    val homeScreenEntry = remember(backStackEntry) { navController.getBackStackEntry(HOME_ROUTE) }
                    CompraFinalizadaScreen(navController = navController, cartViewModel = viewModel(homeScreenEntry))
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
                    BOBackofficeNavGraph(navController = navController)
                }

                // RUTA DE INFO NUTRICIONAL (API EXTERNA)
                composable(Destinos.FOOD_INFO_SCREEN) {
                    FoodInfoScreen(navController = navController)
                }
            }
        }
    }
}
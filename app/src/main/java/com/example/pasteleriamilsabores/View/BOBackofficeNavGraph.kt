package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.Destinos
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.ViewModel.BOViewModelFactory
import kotlinx.coroutines.launch

// Clase de datos para los ítems del menú
data class DrawerItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOBackofficeNavGraph(
    navController: NavController
) {
    val context = LocalContext.current

    // 🛑 INYECCIÓN CORRECTA CON FACTORY
    // Si esta línea falla, el error será "Cannot create instance..."
    val boViewModel: BOViewModel = viewModel(
        factory = BOViewModelFactory(context)
    )

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentScreenRoute by boViewModel.currentScreen.collectAsState()

    // Definición única de ítems del menú
    val drawerItems = remember {
        listOf(
            DrawerItem(Destinos.BODASHBOARD, Icons.Default.Dashboard, "Dashboard"),
            DrawerItem(Destinos.BOORDENES, Icons.Default.ReceiptLong, "Órdenes"),
            DrawerItem(Destinos.BOPRODUCTO, Icons.Default.Cake, "Productos"),
            DrawerItem(Destinos.BOCATEGORIA, Icons.Default.Category, "Categorías"),
            DrawerItem(Destinos.BOUSUARIO, Icons.Default.People, "Usuarios"),
            DrawerItem(Destinos.BOREPORTES, Icons.Default.BarChart, "Reportes"),
            DrawerItem(Destinos.BOPERFIL, Icons.Default.Person, "Perfil")
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Menú Backoffice", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                Divider()

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentScreenRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            boViewModel.navigateTo(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                Spacer(Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        navController.navigate(Destinos.LOGIN_SCREEN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        content = {
            BODashboardContainer(
                currentScreenRoute = currentScreenRoute,
                onMenuClick = { scope.launch { drawerState.open() } },
                boViewModel = boViewModel,
                drawerItems = drawerItems,
                onLogout = {
                    navController.navigate(Destinos.LOGIN_SCREEN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BODashboardContainer(
    currentScreenRoute: String,
    onMenuClick: () -> Unit,
    boViewModel: BOViewModel,
    drawerItems: List<DrawerItem>,
    onLogout: () -> Unit
) {
    val title = remember(currentScreenRoute, drawerItems) {
        drawerItems.find { it.route == currentScreenRoute }?.title ?: "Backoffice"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (currentScreenRoute) {
                Destinos.BODASHBOARD -> BODashboardScreen(boViewModel)
                Destinos.BOORDENES -> BOOrdenesScreen(boViewModel)
                Destinos.BOPRODUCTO -> BOProductoScreen(boViewModel)
                Destinos.BOCATEGORIA -> BOCategoriaScreen(boViewModel)
                Destinos.BOUSUARIO -> BOUsuarioScreen(boViewModel)
                Destinos.BOREPORTES -> BOReportesScreen(boViewModel)
                Destinos.BOPERFIL -> BOPerfilScreen(boViewModel)
                else -> BODashboardScreen(boViewModel)
            }
        }
    }
}
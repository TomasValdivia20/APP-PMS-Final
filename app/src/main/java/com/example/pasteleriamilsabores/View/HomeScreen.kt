package com.example.pasteleriamilsabores.View

// Implementacion Visual de la categoria
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// Implementacion Categoria
import com.example.pasteleriamilsabores.Model.Categoria
import com.example.pasteleriamilsabores.ViewModel.CategoriaViewModel
// Implementacion Cartas De Categoria con fondo Pasteleria y Bienvenida
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
// Para carrito
import com.example.pasteleriamilsabores.ViewModel.CartViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FabPosition
import com.example.pasteleriamilsabores.ui.theme.Amarillo


// Para navegacion
import com.example.pasteleriamilsabores.Destinos // Asumo que Destinos es el objeto con las rutas
import com.example.pasteleriamilsabores.Utils.construirUrlImagen

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaCartas(categoria: Categoria, onClick: (Int, String) -> Unit) {

    val imageUrl = construirUrlImagen(categoria.imagen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable { onClick(categoria.id, categoria.nombre) }, // Llama a la navegación con el ID y el NOMBRE
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // IMAGEN
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = categoria.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // TEXTO INFERIOR (Título y Descripción)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título (Nombre de la Categoría)
                Text(
                    text = categoria.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))

                // DESCRIPCIÓN
                categoria.descripcion?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
// --- FIN DEL COMPONENTE DE TARJETA ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    email: String?,

    // Obtener ViewModels
    categoriaViewModel: CategoriaViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel() // <--- CartViewModel es inyectado correctamente
) {
    val context = LocalContext.current

    // Observar los estados del ViewModel
    val categorias by categoriaViewModel.categorias.collectAsState()
    val loading by categoriaViewModel.loading.collectAsState()

    // 🛑 NUEVO: Observar el número de ítems del carrito
    val cartItemCount by cartViewModel.items.collectAsState()

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        categoriaViewModel.cargarCategorias(context)
    }

    // Usamos Box para apilar la imagen de fondo detrás del Scaffold
    Box(modifier = Modifier.fillMaxSize()) {

        // IMAGEN DE FONDO
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/img/bg.jpg"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.3f)
        )

        Scaffold(
            // Fondo transparente para que se vea el Box
            containerColor = Color.Transparent,

            // TopBar
            topBar = {
                TopAppBar(
                    title = { Text("Bienvenido, ${email ?: "Pastelería"}") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Color para el icono
                    ),
                    // Botón Logout
                    actions = {
                        // BOTÓN DE INFORMACIÓN (API EXTERNA)
                        IconButton(
                            onClick = {
                                navController.navigate(Destinos.FOOD_INFO_SCREEN)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info Nutricional"
                            )
                        }
                        IconButton(
                            onClick = {
                                // Navegar al Login y limpiar TODA la pila
                                navController.navigate(Destinos.LOGIN_SCREEN) {
                                    popUpTo(0) { inclusive = true } // Borra todo el historial
                                }
                            }
                        ) {
                            // Icono de puerta/salida
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión"
                            )
                        }

                    }
                )
            },

            // 🛑 IMPLEMENTACIÓN DEL BOTÓN FLOTANTE FIJO (FAB)
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Destinos.CART_SCREEN) },
                    icon = {
                        // Asegúrate de que Icons.Filled y Icon estén importados
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Ver Carrito")
                    },
                    text = {
                        Text("Ver Carrito (${cartItemCount.size})") // Muestra el conteo de ítems
                    },
                    // Usamos un modificador para asegurar que ocupe gran parte del ancho
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            },

            // Coloca el FAB en el centro inferior, haciéndolo "fijo" visualmente
            floatingActionButtonPosition = FabPosition.Center
            // -------------------------------------------------------------

        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ... (El resto de tu LazyColumn: Títulos, Categorías, etc.)

                // 1. TÍTULO DE BIENVENIDA
                item {
                    Text(
                        text = "Pastelería Mil Sabores",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Amarillo
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    Divider()
                }

                item {
                    Text(
                        "Selecciona una Categoria:",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // --- SECCIÓN DE CATEGORÍAS EN CUADRÍCULA (LazyVerticalGrid) ---
                if (loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    item {
                        val rowCount = (categorias.size + 1) / 2
                        val gridHeight = if (rowCount > 0) (rowCount * 260 + (rowCount - 1) * 12).dp else 0.dp

                        if (gridHeight > 0.dp) {
                            Box(modifier = Modifier.height(gridHeight)) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(0.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    userScrollEnabled = false
                                ) {
                                    items(categorias, key = { it.id }) { categoria ->
                                        CategoriaCartas (categoria = categoria) { categoryId, categoryName ->

                                            val encodedCategoryName = URLEncoder.encode(categoryName, StandardCharsets.UTF_8.toString())

                                            // 🛑 USAMOS EL HELPER PARA CONSTRUIR LA RUTA LIMPIAMENTE
                                            navController.navigate(Destinos.crearRutaProducto(categoryId, encodedCategoryName))
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                "No se encontraron categorías.",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                item {
                    Divider(
                        Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "Pulsa una categoría para ver sus productos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
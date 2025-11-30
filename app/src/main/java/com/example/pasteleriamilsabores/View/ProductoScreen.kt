// View/ProductoScreen.kt
package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.alpha
import com.example.pasteleriamilsabores.Destinos
import androidx.compose.ui.graphics.Color // Asegúrate de tener esta
import coil.compose.rememberAsyncImagePainter // Asegúrate de tener esta
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pasteleriamilsabores.Model.Producto
import com.example.pasteleriamilsabores.Utils.construirUrlImagen
import com.example.pasteleriamilsabores.ViewModel.ProductoViewModel
import com.example.pasteleriamilsabores.ViewModel.ProductoViewModelFactory
import com.example.pasteleriamilsabores.ui.theme.Amarillo // Asumiendo que usas tu color
import java.text.NumberFormat
import java.util.Locale


// Componente para mostrar un solo producto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(producto: Producto, onClick: (Int) -> Unit) {

    // Obtener la URL de la imagen (similar a la lógica en HomeScreen)
    val imageUrl = construirUrlImagen(producto.imagen)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            // Navegar al detalle del producto
            .clickable { onClick(producto.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // IMAGEN
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            // TEXTO INFERIOR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Nombre del Producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))

                // Precio Mínimo
                val precioMinimo = producto.variantes.minOfOrNull { it.precio } ?: producto.precioBase ?: 0
                val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL")) // Formato de moneda chilena

                Text(
                    text = "Desde: ${NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(precioMinimo)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}


// Pantalla Principal de Productos
// View/ProductoScreen.kt - SOLAMENTE LA FUNCIÓN ProductoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(
    categoriaId: Int,
    categoriaNombre: String,
    navController: NavController
) {
    val context = LocalContext.current

    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(context, categoriaId)
    )

    val productos by productoViewModel.productos.collectAsState()
    val loading by productoViewModel.loading.collectAsState()

    // CORRECCIÓN: Reemplazar los signos '+' por un espacio vacío en el nombre de la categoría
    val nombreDecodificado = categoriaNombre.replace('+', ' ')

    val tituloScreen = "Productos de $nombreDecodificado"

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. IMAGEN DE FONDO
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/img/bg.jpg"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.3f)
        )

        // 2. SCAFFOLD - CORREGIDO
        Scaffold(
            // *** ESTO HACE QUE LA IMAGEN DE FONDO SEA VISIBLE ***
            containerColor = Color.Transparent,
            // ****************************************************
            topBar = {
                TopAppBar(
                    title = { Text(tituloScreen) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        // Hacemos el TopAppBar semitransparente para que se integre
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
            ) {
                if (loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else if (productos.isEmpty()) {
                    Text(
                        "No se encontraron productos para $nombreDecodificado.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(productos, key = { it.id }) { producto ->
                            ProductoCard(producto = producto) { productoId ->
                                // 4. Navegación: Envía el ID del producto
                                // Usa el helper del objeto Destinos
                                navController.navigate(Destinos.crearRutaDetalleProducto(productoId))
                            }
                        }
                    }
                }
            }
        }
    }
}
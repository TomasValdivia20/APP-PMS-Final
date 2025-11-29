package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pasteleriamilsabores.Model.VarianteProducto
import com.example.pasteleriamilsabores.ViewModel.DetalleProductoViewModel
import com.example.pasteleriamilsabores.ViewModel.DetalleProductoViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import com.example.pasteleriamilsabores.ViewModel.CartViewModel
import com.example.pasteleriamilsabores.Model.CartItem
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.pasteleriamilsabores.Utils.construirUrlImagen // 🛑 IMPORTAR HELPER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val viewModel: DetalleProductoViewModel = viewModel(
        factory = DetalleProductoViewModelFactory(context, productoId)
    )

    val producto by viewModel.producto.collectAsState()
    val varianteSeleccionada by viewModel.varianteSeleccionada.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/img/bg.jpg"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize().alpha(0.3f)
        )

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(producto?.nombre ?: "Cargando...") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (producto == null) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Cargando detalles...")
                } else {
                    // Usar el helper para la imagen
                    val urlImagen = construirUrlImagen(producto!!.imagen)

                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = urlImagen),
                            contentDescription = producto!!.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(producto!!.nombre, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(8.dp))
                    Text(producto!!.descripcion, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(24.dp))

                    VarianteSelector(
                        producto = producto!!,
                        varianteSeleccionada = varianteSeleccionada,
                        onVarianteSeleccionada = viewModel::seleccionarVariante,
                        formatter = formatter
                    )

                    Spacer(Modifier.height(16.dp))

                    varianteSeleccionada?.let {
                        NutricionPanelSimple(it)
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val selectedVar = varianteSeleccionada
                            val prod = producto

                            if (selectedVar != null && prod != null) {
                                val item = CartItem(
                                    productoId = prod.id,
                                    nombreProducto = prod.nombre,
                                    imagenProducto = prod.imagen,
                                    varianteSeleccionada = selectedVar,
                                    cantidad = 1
                                )
                                cartViewModel.addItem(item)
                                scope.launch {
                                    snackbarHostState.showSnackbar("${prod.nombre} añadido al carrito.")
                                }
                            }
                        },
                        enabled = varianteSeleccionada != null,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Agregar al Carrito", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun VarianteSelector(
    producto: com.example.pasteleriamilsabores.Model.Producto,
    varianteSeleccionada: VarianteProducto?,
    onVarianteSeleccionada: (VarianteProducto) -> Unit,
    formatter: NumberFormat
) {
    val tieneMultiples = producto.variantes.size > 1
    var expanded by remember { mutableStateOf(false) }

    // Helper para formatear texto
    fun formatVariante(v: VarianteProducto) = "${v.nombre} - ${formatter.format(v.precio)}"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Opción y Precio:", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        if (tieneMultiples) {
            Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)) {
                OutlinedTextField(
                    value = varianteSeleccionada?.let { formatVariante(it) } ?: "Seleccione una opción",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar") },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "dropdown", Modifier.clickable { expanded = true }) },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    producto.variantes.forEach { v ->
                        DropdownMenuItem(
                            text = { Text(formatVariante(v)) },
                            onClick = {
                                onVarianteSeleccionada(v)
                                expanded = false
                            }
                        )
                    }
                }
            }
        } else {
            val v = producto.variantes.firstOrNull()
            if (v != null) {
                LaunchedEffect(Unit) { onVarianteSeleccionada(v) }
                Text(text = formatVariante(v), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            } else {
                Text("Precio no disponible", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun NutricionPanelSimple(variante: VarianteProducto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Información Nutricional", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Divider(Modifier.padding(vertical = 8.dp))
            // Mostramos el string directo del backend
            Text(variante.infoNutricional, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
package com.example.pasteleriamilsabores.View

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.Model.Producto
import com.example.pasteleriamilsabores.Model.Categoria
import com.example.pasteleriamilsabores.Model.VarianteProducto
import com.example.pasteleriamilsabores.Utils.construirUrlImagen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOProductoScreen(viewModel: BOViewModel) {
    val productos by viewModel.productos.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }

    var mostrarAlerta by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                productoAEditar = null
                mostrarFormulario = !mostrarFormulario
            }) {
                Icon(if (mostrarFormulario) Icons.Default.Remove else Icons.Default.Add, contentDescription = "Agregar/Cancelar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Gestión de Productos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            if (mostrarFormulario || productoAEditar != null) {
                BOAgregarProductoForm(
                    categorias = categorias,
                    productoExistente = productoAEditar,
                    onGuardar = { prod ->
                        if (productoAEditar != null) {
                            viewModel.actualizarProducto(prod)
                        } else {
                            viewModel.crearProducto(prod)
                        }
                        mostrarFormulario = false
                        productoAEditar = null
                    },
                    onCancelar = {
                        mostrarFormulario = false
                        productoAEditar = null
                    },
                    viewModel = viewModel
                )
                Divider(thickness = 2.dp)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos, key = { it.id }) { producto ->
                    BOProductoItem(
                        producto = producto,
                        formatter = formatter,
                        onDelete = {
                            productoAEliminar = producto
                            mostrarAlerta = true
                        },
                        onEdit = {
                            productoAEditar = producto
                            mostrarFormulario = true
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }

            AlertaEliminar(
                show = mostrarAlerta,
                titulo = "¿Eliminar Producto?",
                mensaje = "¿Estás seguro de que quieres eliminar '${productoAEliminar?.nombre}'?",
                onConfirm = {
                    productoAEliminar?.let { viewModel.eliminarProducto(it.id) }
                },
                onDismiss = { mostrarAlerta = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOAgregarProductoForm(
    categorias: List<Categoria>,
    productoExistente: Producto? = null,
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit,
    viewModel: BOViewModel
) {
    var nombre by remember(productoExistente) { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember(productoExistente) { mutableStateOf(productoExistente?.descripcion ?: "") }
    var precioBase by remember(productoExistente) { mutableStateOf(productoExistente?.precioBase?.toString() ?: "") }
    var imagenUrl by remember(productoExistente) { mutableStateOf(productoExistente?.imagen ?: "placeholder.jpg") }

    var categoriaSeleccionada by remember(productoExistente, categorias) {
        mutableStateOf(categorias.find { it.id == productoExistente?.categoria?.id })
    }

    var expandedCat by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.subirImagen(uri) { urlSubida ->
                    imagenUrl = urlSubida
                }
            }
        }
    )

    // Importante: Convertir la lista inmutable del producto a una lista mutable para edición
    val variantesList = remember(productoExistente) {
        if (productoExistente != null) {
            productoExistente.variantes.toMutableStateList()
        } else {
            mutableStateListOf()
        }
    }

    var varNombre by remember { mutableStateOf("") }
    var varPrecio by remember { mutableStateOf("") }
    var varInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (productoExistente != null) "Editar Producto #${productoExistente.id}" else "Nuevo Producto",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = {
                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (imagenUrl == "placeholder.jpg") "Subir Imagen" else "Imagen Cargada")
        }

        if (imagenUrl != "placeholder.jpg") {
            AsyncImage(
                model = construirUrlImagen(imagenUrl),
                contentDescription = null,
                modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
            )
        }

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Producto") }, modifier = Modifier.fillMaxWidth())

        ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = !expandedCat }) {
            OutlinedTextField(
                value = categoriaSeleccionada?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                placeholder = { Text("Seleccione Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                if (categorias.isEmpty()) {
                    DropdownMenuItem(text = { Text("No hay categorías cargadas") }, onClick = {})
                }
                categorias.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.nombre) },
                        onClick = { categoriaSeleccionada = cat; expandedCat = false }
                    )
                }
            }
        }

        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = precioBase,
            onValueChange = { precioBase = it },
            label = { Text("Precio Base") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Divider()
        Text("Variantes / Tamaños", style = MaterialTheme.typography.titleMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedTextField(value = varNombre, onValueChange = { varNombre = it }, label = { Text("Nombre (ej: 12p)") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = varPrecio, onValueChange = { varPrecio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(0.8f))
        }
        OutlinedTextField(value = varInfo, onValueChange = { varInfo = it }, label = { Text("Info Nutricional") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                if (varNombre.isNotBlank() && varPrecio.isNotBlank()) {
                    // ID 0 indica nueva variante para el backend (incluso si el producto existe)
                    variantesList.add(VarianteProducto(0, varNombre, varPrecio.toIntOrNull() ?: 0, 100, varInfo))
                    varNombre = ""
                    varPrecio = ""
                    varInfo = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Añadir Variante")
        }

        if (variantesList.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    variantesList.forEachIndexed { index, variante ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ${variante.nombre} ($${variante.precio})")
                            IconButton(onClick = { variantesList.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        Divider()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val prod = Producto(
                        id = productoExistente?.id ?: 0,
                        nombre = nombre,
                        descripcion = descripcion,
                        imagen = imagenUrl,
                        precioBase = precioBase.toIntOrNull() ?: 0,
                        categoria = categoriaSeleccionada,
                        variantes = variantesList.toList() // Enviamos la lista completa (nuevas + existentes)
                    )
                    onGuardar(prod)
                },
                modifier = Modifier.weight(1f),
                enabled = nombre.isNotBlank() && categoriaSeleccionada != null
            ) { Text(if (productoExistente != null) "Actualizar" else "Guardar") }

            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
        }
    }
}

@Composable
fun BOProductoItem(
    producto: Producto,
    formatter: NumberFormat,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = construirUrlImagen(producto.imagen),
                contentDescription = null,
                modifier = Modifier.size(50.dp).padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Cat: ${producto.categoria?.nombre ?: "Sin Categ."}", style = MaterialTheme.typography.bodySmall)

                if (producto.variantes.isNotEmpty()) {
                    Text(
                        "${producto.variantes.size} variantes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}
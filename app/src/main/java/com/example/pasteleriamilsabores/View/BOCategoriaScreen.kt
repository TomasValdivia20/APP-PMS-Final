package com.example.pasteleriamilsabores.View

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable // Importar clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit // Icono editar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.Model.Categoria
import com.example.pasteleriamilsabores.Utils.construirUrlImagen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOCategoriaScreen(viewModel: BOViewModel) {
    val categorias by viewModel.categorias.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    //  ESTADO PARA EDICIÓN: Guarda la categoría que se va a editar
    var categoriaAEditar by remember { mutableStateOf<Categoria?>(null) }

    var mostrarAlerta by remember { mutableStateOf(false) }
    var categoriaAEliminar by remember { mutableStateOf<Categoria?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Al crear nuevo, limpiamos el estado de edición
                categoriaAEditar = null
                mostrarFormulario = !mostrarFormulario
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Gestión de Categorías", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

            // Si hay una categoría para editar, mostramos el formulario automáticamente
            if (mostrarFormulario || categoriaAEditar != null) {
                Divider()
                BOAgregarCategoriaForm(
                    categoriaExistente = categoriaAEditar, // Pasamos la categoría actual
                    onGuardar = { categoriaGuardada ->
                        if (categoriaAEditar != null) {
                            // MODO ACTUALIZAR
                            viewModel.actualizarCategoria(categoriaGuardada)
                        } else {
                            // MODO CREAR
                            viewModel.crearCategoria(categoriaGuardada)
                        }
                        mostrarFormulario = false
                        categoriaAEditar = null // Limpiar selección
                    },
                    onCancelar = {
                        mostrarFormulario = false
                        categoriaAEditar = null
                    },
                    viewModel = viewModel
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias, key = { it.id }) { categoria ->
                    BOCategoriaItem(
                        categoria = categoria,
                        onDelete = {
                            categoriaAEliminar = categoria
                            mostrarAlerta = true
                        },
                        //  AL HACER CLICK, ACTIVAMOS MODO EDICIÓN
                        onClick = {
                            categoriaAEditar = categoria
                            mostrarFormulario = true // Forzar mostrar formulario
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }

            AlertaEliminar(
                show = mostrarAlerta,
                titulo = "¿Eliminar Categoría?",
                mensaje = "Se eliminará '${categoriaAEliminar?.nombre}' y TODOS sus productos.",
                onConfirm = { categoriaAEliminar?.let { viewModel.eliminarCategoria(it.id) } },
                onDismiss = { mostrarAlerta = false }
            )
        }
    }
}

@Composable
fun BOAgregarCategoriaForm(
    categoriaExistente: Categoria? = null, // Parámetro opcional
    onGuardar: (Categoria) -> Unit,
    onCancelar: () -> Unit,
    viewModel: BOViewModel
) {
    // Inicializamos los estados con los valores existentes si estamos editando
    var nombre by remember(categoriaExistente) { mutableStateOf(categoriaExistente?.nombre ?: "") }
    var descripcion by remember(categoriaExistente) { mutableStateOf(categoriaExistente?.descripcion ?: "") }
    var imagenUrl by remember(categoriaExistente) { mutableStateOf(categoriaExistente?.imagen ?: "placeholder.jpg") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.subirImagen(uri) { imagenUrl = it }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            if (categoriaExistente != null) "Editar Categoría #${categoriaExistente.id}" else "Nueva Categoría",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (imagenUrl == "placeholder.jpg") "Subir Imagen" else "Imagen Cargada")
        }
        // Vista previa pequeña
        if (imagenUrl != "placeholder.jpg") {
            AsyncImage(model = construirUrlImagen(imagenUrl), contentDescription = null, modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally))
        }

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    // Mantenemos el ID si existe, o 0 si es nuevo
                    val cat = Categoria(id = categoriaExistente?.id ?: 0, nombre = nombre, descripcion = descripcion, imagen = imagenUrl)
                    onGuardar(cat)
                },
                modifier = Modifier.weight(1f),
                enabled = nombre.isNotBlank()
            ) { Text(if (categoriaExistente != null) "Actualizar" else "Guardar") }
            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
        }
    }
}

@Composable
fun BOCategoriaItem(categoria: Categoria, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = construirUrlImagen(categoria.imagen),
                contentDescription = null,
                modifier = Modifier.size(50.dp).padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(categoria.descripcion ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            // Botón explícito de editar (opcional, ya que el card es clickeable)
            IconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}
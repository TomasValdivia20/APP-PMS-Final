package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.Model.UsuarioBackoffice
import com.example.pasteleriamilsabores.Model.Rol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOUsuarioScreen(viewModel: BOViewModel) {
    val usuarios by viewModel.usuariosReales.collectAsState()
    val usuarioActual by viewModel.usuario.collectAsState() // Necesitamos saber quién es el admin actual

    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<UsuarioBackoffice?>(null) }

    // Estados para Alerta de Eliminación
    var mostrarAlerta by remember { mutableStateOf(false) }
    var usuarioAEliminar by remember { mutableStateOf<UsuarioBackoffice?>(null) }

    // SEGURIDAD: Solo ADMIN puede ver/gestionar esta pantalla
    // Verificamos nulos por seguridad
    if (usuarioActual == null || usuarioActual?.rol?.nombre != "ADMIN") {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Acceso Restringido", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                Text("Solo Administradores pueden gestionar usuarios.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                usuarioAEditar = null
                mostrarFormulario = !mostrarFormulario
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Gestión de Usuarios",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            // Mostrar Formulario si corresponde
            if (mostrarFormulario || usuarioAEditar != null) {
                BOAgregarUsuarioForm(
                    usuarioExistente = usuarioAEditar,
                    onGuardar = { user ->
                        if (usuarioAEditar != null) viewModel.actualizarUsuario(user)
                        else viewModel.crearUsuario(user)

                        mostrarFormulario = false
                        usuarioAEditar = null
                    },
                    onCancelar = {
                        mostrarFormulario = false
                        usuarioAEditar = null
                    }
                )
                Divider(thickness = 2.dp)
            }

            if (usuarios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando usuarios o lista vacía...")
                }
            } else {
                // Cabecera de Tabla
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("ID", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                    Text("Usuario", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                    Text("Rol", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(48.dp)) // Espacio para botones
                }
                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(usuarios, key = { it.id }) { usuario ->
                        BOUsuarioItem(
                            usuario = usuario,
                            onEdit = {
                                usuarioAEditar = usuario
                                mostrarFormulario = true
                            },
                            onDelete = {
                                usuarioAEliminar = usuario
                                mostrarAlerta = true
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            // Diálogo de Confirmación de Eliminación
            if (mostrarAlerta) {
                AlertDialog(
                    onDismissRequest = { mostrarAlerta = false },
                    title = { Text("¿Eliminar Usuario?") },
                    text = { Text("¿Estás seguro de que deseas eliminar a ${usuarioAEliminar?.nombre}? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                usuarioAEliminar?.let { viewModel.eliminarUsuario(it.id) }
                                mostrarAlerta = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Eliminar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { mostrarAlerta = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOAgregarUsuarioForm(
    usuarioExistente: UsuarioBackoffice?,
    onGuardar: (UsuarioBackoffice) -> Unit,
    onCancelar: () -> Unit
) {
    // Estados del formulario
    var nombre by remember(usuarioExistente) { mutableStateOf(usuarioExistente?.nombre ?: "") }
    var apellido by remember(usuarioExistente) { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var correo by remember(usuarioExistente) { mutableStateOf(usuarioExistente?.correo ?: "") }
    var rut by remember(usuarioExistente) { mutableStateOf(usuarioExistente?.rut ?: "") }
    var password by remember { mutableStateOf("") } // Password vacía por defecto (solo se envía si se cambia)

    // Roles disponibles (Hardcoded para simplificar, pero coinciden con Backend)
    val roles = listOf(Rol(1, "ADMIN"), Rol(2, "CLIENTE"), Rol(3, "EMPLEADO"))

    // Selección de Rol
    var rolSeleccionado by remember(usuarioExistente) {
        mutableStateOf(roles.find { it.nombre == usuarioExistente?.rol?.nombre } ?: roles[1])
    }
    var expandedRol by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(max = 400.dp) // Limitar altura
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (usuarioExistente != null) "Editar Usuario #${usuarioExistente.id}" else "Nuevo Usuario",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (usuarioExistente != null) "Nueva Contraseña (Opcional)" else "Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown de Rol
        ExposedDropdownMenuBox(expanded = expandedRol, onExpandedChange = { expandedRol = !expandedRol }) {
            OutlinedTextField(
                value = rolSeleccionado.nombre,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedRol, onDismissRequest = { expandedRol = false }) {
                roles.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol.nombre) },
                        onClick = { rolSeleccionado = rol; expandedRol = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val user = UsuarioBackoffice(
                        id = usuarioExistente?.id ?: 0,
                        rut = rut,
                        nombre = nombre,
                        apellido = apellido,
                        correo = correo,
                        rol = rolSeleccionado
                        // Nota: La contraseña se enviará aparte o el backend la ignora si no está en el modelo UsuarioBackoffice.
                        // Si necesitas enviarla, deberías usar un DTO específico o adaptar el modelo.
                        // Para este ejemplo, asumimos que el backend maneja la actualización básica.
                    )
                    onGuardar(user)
                },
                modifier = Modifier.weight(1f),
                enabled = nombre.isNotBlank() && correo.isNotBlank()
            ) { Text(if (usuarioExistente != null) "Actualizar" else "Crear") }

            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
        }
    }
}

@Composable
fun BOUsuarioItem(usuario: UsuarioBackoffice, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "#${usuario.id}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.5f))

        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = "${usuario.nombre} ${usuario.apellido}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = usuario.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = when (usuario.rol.nombre) {
                    "ADMIN" -> MaterialTheme.colorScheme.primaryContainer
                    "EMPLEADO" -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = usuario.rol.nombre,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Acciones
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.Model.UsuarioBackoffice // <-- Importar modelo

@Composable
fun BOUsuarioScreen(viewModel: BOViewModel) {
    // Observar la lista de usuarios
    val usuarios by viewModel.usuarios.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            "Gestión de Usuarios",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de usuarios
        if (usuarios.isEmpty()) {
            Text("No hay usuarios registrados.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cabecera de la "tabla" (opcional)
                item {
                    Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        Text("Nombre", Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Text("Correo", Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold)
                        Text("Rol", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold)
                    }
                    Divider()
                }

                // Filas de usuarios
                items(usuarios, key = { it.id }) { usuario ->
                    BOUsuarioItem(usuario = usuario)
                    Divider() // Separador entre usuarios
                }
            }
        }
        // En el futuro: Botón para agregar usuario (no funcional)
    }
}

// Composable para mostrar un ítem de usuario
@Composable
fun BOUsuarioItem(usuario: UsuarioBackoffice) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Padding vertical para cada fila
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre Completo
        Text(
            text = "${usuario.nombre} ${usuario.apellido}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        // Correo
        Text(
            text = usuario.correo,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Acortar si es muy largo
        )
        // Rol
        Text(
            text = usuario.rol,
            modifier = Modifier.weight(0.7f),
            style = MaterialTheme.typography.bodyMedium,
            color = if (usuario.rol == "Administrador") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Aquí podrías añadir botones de Editar/Eliminar (no funcionales)
    }
}
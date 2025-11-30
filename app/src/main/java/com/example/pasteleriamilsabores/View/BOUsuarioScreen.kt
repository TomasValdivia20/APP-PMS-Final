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
import com.example.pasteleriamilsabores.Model.UsuarioBackoffice

@Composable
fun BOUsuarioScreen(viewModel: BOViewModel) {
    // 🛑 DATOS REALES: Observamos usuariosReales
    val usuarios by viewModel.usuariosReales.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Gestión de Usuarios",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (usuarios.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cargando usuarios o lista vacía...")
            }
        } else {
            // Cabecera
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("ID", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                Text("Usuario", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                Text("Rol", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(usuarios, key = { it.id }) { usuario ->
                    BOUsuarioItem(usuario = usuario)
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun BOUsuarioItem(usuario: UsuarioBackoffice) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "#${usuario.id}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.5f))

        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = "${usuario.nombre} ${usuario.apellido}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = usuario.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Accedemos al nombre del rol dentro del objeto Rol
        val nombreRol = usuario.rol.nombre

        Surface(
            color = when (nombreRol) {
                "ADMIN" -> MaterialTheme.colorScheme.primaryContainer
                "EMPLEADO" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = nombreRol,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
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
import com.example.pasteleriamilsabores.Model.OrdenResponse
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BOOrdenesScreen(viewModel: BOViewModel) {
    // 🛑 DATOS REALES: Observamos ordenesReales
    val ordenes by viewModel.ordenesReales.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Registro de Órdenes",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (ordenes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay órdenes registradas.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ordenes, key = { it.id }) { orden ->
                    BOOrdenItem(orden = orden, formatter = formatter)
                }
            }
        }
    }
}

@Composable
fun BOOrdenItem(orden: OrdenResponse, formatter: NumberFormat) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
                // Fecha simple (primeros 10 chars: YYYY-MM-DD)
                Text(
                    text = if (orden.fecha.length >= 10) orden.fecha.take(10) else orden.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Cliente:", style = MaterialTheme.typography.labelMedium)
                    Text("${orden.usuario.nombre} ${orden.usuario.apellido}", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total:", style = MaterialTheme.typography.labelMedium)
                    Text(
                        formatter.format(orden.total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Surface(
                color = if (orden.estado == "PAGADO") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = orden.estado,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import com.example.pasteleriamilsabores.Model.OrdenResponse
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BOOrdenesScreen(viewModel: BOViewModel) {
    // Observamos la lista de órdenes reales
    val ordenes by viewModel.ordenesReales.collectAsState()

    // Formateador de moneda para Chile
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Registro de Órdenes",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (ordenes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay órdenes registradas (o cargando...).")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ordenes, key = { it.id }) { orden ->
                    BOOrdenItem(
                        orden = orden,
                        formatter = formatter,
                        onEstadoChange = { nuevoEstado ->
                            viewModel.cambiarEstadoOrden(orden.id, nuevoEstado)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOOrdenItem(
    orden: OrdenResponse,
    formatter: NumberFormat,
    onEstadoChange: (String) -> Unit
) {
    val estadosPosibles = listOf("PENDIENTE", "PROCESANDO", "ENTREGADO", "CANCELADO", "COMPLETADA")
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado: ID y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
                // Parseo simple de fecha (YYYY-MM-DD)
                Text(
                    text = if (orden.fecha.length >= 10) orden.fecha.take(10) else orden.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Detalles: Cliente y Total
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

            Spacer(Modifier.height(12.dp))

            // Selector de Estado (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = orden.estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        // Color del texto según estado (verde si completado/entregado)
                        focusedTextColor = if (orden.estado == "ENTREGADO" || orden.estado == "COMPLETADA") Color(0xFF4CAF50) else Color.Black,
                        unfocusedTextColor = if (orden.estado == "ENTREGADO" || orden.estado == "COMPLETADA") Color(0xFF4CAF50) else Color.Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estadosPosibles.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                onEstadoChange(estado)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
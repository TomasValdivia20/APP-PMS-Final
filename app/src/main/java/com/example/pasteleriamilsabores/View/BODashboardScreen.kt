package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.Model.OrdenResponse
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BODashboardScreen(viewModel: BOViewModel) {
    val ordenes: List<OrdenResponse> by viewModel.ordenesReales.collectAsState(initial = emptyList())
    val reportes by viewModel.reporteVentas.collectAsState()

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Resumen Financiero", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        }

        // Tarjetas con datos reales del reporte
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Usamos DashboardCard de BOComponentesComunes (si no lo tienes, copia la definición aquí abajo o importala)
                // Asumimos que está en el mismo paquete View y no necesita import.
                DashboardCard(
                    title = "Ventas del Mes",
                    content = {
                        Text(
                            text = if (reportes != null) formatter.format(reportes!!.mensual) else "...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Ventas Anuales",
                    content = {
                        Text(
                            text = if (reportes != null) formatter.format(reportes!!.anual) else "...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Últimas Órdenes", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))
        }

        if (ordenes.isEmpty()) {
            item { Text("No hay movimientos recientes.") }
        } else {
            // Mostramos las últimas 5 órdenes solamente
            items(
                items = ordenes.take(5),
                key = { it.id }
            ) { orden ->
                // Reutilizamos el item de orden (asegúrate de que BOOrdenItem sea accesible o defínelo aquí)
                // Si BOOrdenItem está en otro archivo del mismo paquete, no necesita import.
                // Si no, copia su definición aquí o importalo.
                // Como alternativa, uso DashboardOrdenRow definido abajo para ser autocontenido.
                DashboardOrdenRow(orden = orden, formatter = formatter)
            }
        }
    }
}

// Componente local para mostrar la orden en el Dashboard (simplificado)
@Composable
fun DashboardOrdenRow(orden: OrdenResponse, formatter: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Orden #${orden.id}", fontWeight = FontWeight.Bold)
                Text(
                    text = if (orden.fecha.length >= 10) orden.fecha.take(10) else orden.fecha,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${orden.usuario.nombre} ${orden.usuario.apellido}", style = MaterialTheme.typography.bodyMedium)
                Text(formatter.format(orden.total), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text(orden.estado, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Si DashboardCard no está accesible desde BOComponentesComunes, descomentar esto:
/*
@Composable
fun DashboardCard(title: String, content: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}
*/
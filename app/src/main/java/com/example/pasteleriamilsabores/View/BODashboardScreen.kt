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

// Reutilizamos BOOrdenItem ya que es idéntico a lo que queremos mostrar aquí
// Si prefieres mantener VentaRow antigua, avísame, pero usar BOOrdenItem es más consistente.

@Composable
fun BODashboardScreen(viewModel: BOViewModel) {
    // 🛑 DATOS REALES
    val ordenes by viewModel.ordenesReales.collectAsState()
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
                DashboardCard(
                    title = "Ventas del Mes",
                    content = {
                        Text(
                            text = if (reportes != null) formatter.format(reportes!!.mensual) else "Cargando...",
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
                            text = if (reportes != null) formatter.format(reportes!!.anual) else "Cargando...",
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
            items(items = ordenes.take(5), key = { it.id }) { orden ->
                BOOrdenItem(orden = orden, formatter = formatter)
            }
        }
    }
}
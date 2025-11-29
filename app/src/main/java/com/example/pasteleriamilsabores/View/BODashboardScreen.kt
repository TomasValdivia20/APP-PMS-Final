package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Necesario para 'by'
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.Model.Venta
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BODashboardScreen(viewModel: BOViewModel) {
    // 🛑 CORRECCIÓN 1: Especificamos explícitamente el tipo <Venta> en emptyList
    // y el tipo de la variable para ayudar al compilador.
    val ventas: List<Venta> by viewModel.ventas.collectAsState(initial = emptyList<Venta>())

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Resumen de Ventas", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(title = "Ventas 15 días", content = { PlaceholderGrafico("Gráfico Líneas") }, modifier = Modifier.weight(1f))
                DashboardCard(title = "Ventas Semestre", content = { PlaceholderGrafico("Gráfico Barras") }, modifier = Modifier.weight(1f))
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Últimas Ventas", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(8.dp))
        }

        // 🛑 CORRECCIÓN 2: Especificamos el tipo 'Venta' en la lambda de key
        items(
            items = ventas,
            key = { venta: Venta -> "${venta.montoTotal}-${venta.fechaCompra}" }
        ) { venta ->
            VentaRow(venta = venta, formatter = formatter)
        }
    }
}
package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BOReportesScreen(viewModel: BOViewModel) {
    // 🛑 DATOS REALES
    val reportes by viewModel.reporteVentas.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Reportes de Ventas (Total Recaudado)", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))

        if (reportes == null) {
            Text("Cargando datos del servidor...")
        } else {
            // 1. Mensual
            DashboardCard(
                title = "Total Mensual (Mes Actual)",
                content = {
                    Text(
                        text = formatter.format(reportes!!.mensual),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Semestral
            DashboardCard(
                title = "Total Semestral (Últimos 6 meses)",
                content = {
                    Text(
                        text = formatter.format(reportes!!.semestral),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Anual
            DashboardCard(
                title = "Total Anual (Año en curso)",
                content = {
                    Text(
                        text = formatter.format(reportes!!.anual),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
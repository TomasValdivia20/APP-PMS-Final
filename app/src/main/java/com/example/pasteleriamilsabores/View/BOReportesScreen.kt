package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel

@Composable
fun BOReportesScreen(viewModel: BOViewModel) {
    // No necesitamos observar datos específicos del ViewModel para los placeholders

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite scroll si se añaden más reportes
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre los elementos
    ) {
        // Título de la sección
        Text(
            "Reportes de Ventas",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        // --- Gráficos (Reutilizando componentes) ---

        // Gráfico Ventas 15 Días
        DashboardCard(
            title = "Ventas en los últimos 15 días",
            content = { PlaceholderGrafico(label = "Gráfico de Líneas Ficticio") },
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
        )

        // Gráfico Ventas Semestre
        DashboardCard(
            title = "Ventas primer semestre",
            content = { PlaceholderGrafico(label = "Gráfico de Barras Ficticio") },
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho
        )

        // Mensaje para futura expansión
        Spacer(Modifier.height(16.dp))
        Text(
            "Aquí se mostrarán más detalles y filtros de reportes en el futuro.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
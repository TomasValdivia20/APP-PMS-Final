package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import java.text.NumberFormat // Importación para el formateador de moneda
import java.util.Locale     // Importación para el Locale

@Composable
fun BOOrdenesScreen(viewModel: BOViewModel) {
    // Observamos la lista de ventas desde el ViewModel
    val ventas by viewModel.ventas.collectAsState()
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding general para la pantalla
    ) {
        // Título de la sección
        Text(
            "Registro de Órdenes Recibidas",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp) // Espacio después del título
        )

        // Lista de órdenes/ventas
        if (ventas.isEmpty()) {
            // Mensaje si no hay ventas (poco probable con datos ficticios)
            Text("No hay órdenes registradas.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada Card
            ) {
                // Iteramos sobre la lista de ventas
                items(ventas, key = { it.fechaCompra + it.nombreCliente }) { venta ->
                    // Reutilizamos el Composable VentaRow que ya definimos
                    // Asegúrate de que VentaRow esté accesible (en BOComponentesComunes.kt)
                    VentaRow(venta = venta, formatter = formatter)
                }
            }
        }
    }
}

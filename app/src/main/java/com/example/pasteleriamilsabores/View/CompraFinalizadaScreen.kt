package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.Destinos
import com.example.pasteleriamilsabores.Model.CartItem
import com.example.pasteleriamilsabores.ViewModel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraFinalizadaScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    // Obtenemos snapshots de los valores actuales para mostrarlos estáticos (recibo)
    val itemsSnapshot = remember { cartViewModel.items.value }
    val subtotalSnapshot = remember { cartViewModel.subtotalTotal.value }
    val descuentoSnapshot = remember { cartViewModel.descuentoTotal.value }
    val totalSnapshot = remember { cartViewModel.totalPagar.value }

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    // Limpiamos el carrito al entrar (efecto secundario)
    LaunchedEffect(Unit) {
        cartViewModel.limpiarCarrito()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Confirmación de Compra") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- MENSAJE DE ÉXITO ---
            Text(
                "¡Compra Exitosa!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "¡Gracias por preferirnos! Se le enviará a su correo la boleta de pago, ¡esperemos que disfrute su pedido!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            // --- RESUMEN DEL PEDIDO ---
            Text("Resumen del Pedido:", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemsSnapshot, key = { it.id }) { item ->
                    ItemCompradoRow(item = item, formatter = formatter)
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))

                    // Totales
                    ResumenRowFinal("Subtotal", subtotalSnapshot, formatter)
                    if (descuentoSnapshot > 0) {
                        ResumenRowFinal("Descuento", -descuentoSnapshot, formatter, color = MaterialTheme.colorScheme.error)
                    }
                    ResumenRowFinal("Total Pagado", totalSnapshot, formatter, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- BOTÓN VOLVER ---
            Button(
                onClick = {
                    navController.popBackStack(route = Destinos.HOME_SCREEN, inclusive = false)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Volver al Catálogo Principal", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ItemCompradoRow(item: CartItem, formatter: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${item.nombreProducto} (x${item.cantidad})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.varianteSeleccionada.nombre, // Usamos el nombre de la variante
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatter.format(item.subtotal),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ResumenRowFinal(
    label: String,
    amount: Int,
    formatter: NumberFormat,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = style, color = color)
        Text(formatter.format(amount), style = style, color = color)
    }
}
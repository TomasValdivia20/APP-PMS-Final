package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.Destinos
import com.example.pasteleriamilsabores.Model.CartItem
import com.example.pasteleriamilsabores.ViewModel.CartViewModel
import com.example.pasteleriamilsabores.ViewModel.AuthViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel, // Recibido como parámetro
    authViewModel: AuthViewModel = viewModel()
) {
    val items by cartViewModel.items.collectAsState()
    val subtotal by cartViewModel.subtotalTotal.collectAsState()
    val descuento by cartViewModel.descuentoTotal.collectAsState()
    val total by cartViewModel.totalPagar.collectAsState()
    val codigoAplicado by cartViewModel.descuentoCodigo.collectAsState()
    val usuarioActual by authViewModel.usuarioActual.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío.", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lista de ítems
                    items(items, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            formatter = formatter,
                            onRemove = { cartViewModel.removeItem(item.id) }
                        )
                    }

                    item { Divider() }

                    // RESUMEN CON EL CÓDIGO DE DSCTO
                    item {
                        CompraResumen(
                            subtotal = subtotal,
                            descuento = descuento,
                            total = total,
                            codigoAplicado = codigoAplicado,
                            formatter = formatter,
                            onValidarCodigo = { codigo -> cartViewModel.validarCodigo(codigo) }
                        )
                    }
                }

                CheckoutButtons(
                    onCompraExitosa = {
                        //  VALIDACIÓN DE USUARIO
                        val uid = usuarioActual?.id?.toLong()

                        if (uid != null && uid > 0) {
                            cartViewModel.finalizarCompraBackend(
                                usuarioId = uid,
                                onSuccess = {
                                    navController.navigate(Destinos.COMPRA_FINALIZADA_SCREEN) {
                                        popUpTo(Destinos.CART_SCREEN) { inclusive = true }
                                    }
                                },
                                onError = { errorMsg ->
                                    scope.launch { snackbarHostState.showSnackbar("Error: $errorMsg") }
                                }
                            )
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Error: Usuario no identificado. Inicie sesión nuevamente.") }
                        }
                    },
                    onCompraError = { }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, formatter: NumberFormat, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreProducto, style = MaterialTheme.typography.titleMedium)
                Text(
                    item.varianteSeleccionada.nombre,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(4.dp))
                Text(formatter.format(item.subtotal), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

// 🛑 COMPONENTE RESTAURADO CON CAMPO DE CÓDIGO
@Composable
fun CompraResumen(
    subtotal: Int,
    descuento: Int,
    total: Int,
    codigoAplicado: String?,
    formatter: NumberFormat,
    onValidarCodigo: (String) -> String
) {
    var codigoInput by remember { mutableStateOf("") }
    var mensajeValidacion by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(vertical = 16.dp)) {

        // --- Fila para ingresar el código ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = codigoInput,
                onValueChange = { codigoInput = it },
                label = { Text("Código de Descuento") },
                placeholder = { Text(codigoAplicado ?: "Ej: PMS50AGNOS") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = codigoAplicado == null // Deshabilitar si ya se aplicó (opcional)
            )
            Button(
                onClick = {
                    mensajeValidacion = onValidarCodigo(codigoInput)
                    // Limpiar input si es exitoso es opcional, pero buena práctica
                    if (codigoAplicado != null) codigoInput = ""
                },
                enabled = codigoInput.isNotBlank()
            ) {
                Text("Aplicar")
            }
        }

        // Mensaje de validación (éxito o error)
        if (mensajeValidacion != null) {
            Text(
                text = mensajeValidacion!!,
                style = MaterialTheme.typography.bodySmall,
                color = if (codigoAplicado != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Resumen de valores
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Subtotal:", style = MaterialTheme.typography.bodyLarge)
            Text(formatter.format(subtotal), style = MaterialTheme.typography.bodyLarge)
        }

        if (descuento > 0) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Descuento:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                Text("- ${formatter.format(descuento)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Total:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(formatter.format(total), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun CheckoutButtons(onCompraExitosa: () -> Unit, onCompraError: () -> Unit) {
    Button(
        onClick = onCompraExitosa,
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text("Finalizar Compra", style = MaterialTheme.typography.titleMedium)
    }
}
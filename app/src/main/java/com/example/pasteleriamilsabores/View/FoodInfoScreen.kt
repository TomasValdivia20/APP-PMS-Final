package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.Model.FdcFood
import com.example.pasteleriamilsabores.ViewModel.FoodInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodInfoScreen(
    navController: NavController,
    viewModel: FoodInfoViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val resultados by viewModel.searchResults.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Info Nutricional (USDA)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra de Búsqueda
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar alimento (ej: Apple)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.buscarAlimento(query) },
                    enabled = !loading && query.isNotBlank()
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Mensaje de Error
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            // Lista de Resultados
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(resultados) { food ->
                    FoodItemCard(food)
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(food: FdcFood) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = food.description, style = MaterialTheme.typography.titleMedium)
            if (!food.brandOwner.isNullOrBlank()) {
                Text(text = "Marca: ${food.brandOwner}", style = MaterialTheme.typography.bodySmall)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Mostrar primeros 3 nutrientes principales
            food.foodNutrients?.take(4)?.forEach { nutrient ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = nutrient.nutrientName, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${nutrient.value} ${nutrient.unitName}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
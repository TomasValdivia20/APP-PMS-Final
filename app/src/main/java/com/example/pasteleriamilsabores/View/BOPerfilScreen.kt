package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.ViewModel.BOViewModel
import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BOPerfilScreen(viewModel: BOViewModel) {
    // Observar el usuario actual del ViewModel
    val usuario by viewModel.usuario.collectAsState()

    // Estados locales para los campos editables del formulario
    // Se inicializan con los valores actuales del usuario
    var nombreState by remember(usuario) { mutableStateOf(usuario.nombre) }
    var apellidoState by remember(usuario) { mutableStateOf(usuario.apellido) }
    var correoState by remember(usuario) { mutableStateOf(usuario.correo) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite scroll si el contenido es largo
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centrar botón
    ) {
        // Título
        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)
        )

        // --- Formulario de Edición ---

        // Campo Nombre
        OutlinedTextField(
            value = nombreState,
            onValueChange = { nombreState = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))

        // Campo Apellido
        OutlinedTextField(
            value = apellidoState,
            onValueChange = { apellidoState = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))

        // Campo Correo
        OutlinedTextField(
            value = correoState,
            onValueChange = { correoState = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))

        // Campo Rol (Solo lectura)
        OutlinedTextField(
            value = usuario.rol,
            onValueChange = {}, // No editable
            readOnly = true,
            label = { Text("Rol") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false // 'enabled = false' aplica el estilo deshabilitado por defecto
        )


        Spacer(Modifier.height(32.dp))

        // Botón Guardar Cambios (Simulación)
        Button(
            onClick = {
                // Llamar a la función de simulación en el ViewModel
                viewModel.simularActualizarPerfil(nombreState, correoState)
                // En una app real, aquí mostrarías un mensaje de éxito/error
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Botón un poco más pequeño que el ancho total
                .height(50.dp)
        ) {
            Text("Guardar Cambios")
        }
    }
}

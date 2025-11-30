package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.ViewModel.AuthViewModel
import com.example.pasteleriamilsabores.ui.theme.PastelCalido
import com.example.pasteleriamilsabores.Model.RegionesChile
import com.example.pasteleriamilsabores.Utils.validarCampos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    // Estados (sin cambios)
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Control de menús desplegables
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedComuna by remember { mutableStateOf(false) }

    // Lógica de filtrado de comunas
    val comunasDisponibles = RegionesChile.comunasPorRegion[region] ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCalido)
            .padding(20.dp)
    ) {
        // Logo (sin cambios)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de Pastelería Mil Sabores",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(120.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            Text(text = "¡Registrate!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            // Campos de texto (Nombre, Apellido, RUT - sin cambios)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = rut,
                onValueChange = { rut = it },
                label = { Text("RUT (12345678-9)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // --- SELECTOR DE REGIÓN (CORREGIDO) ---
            ExposedDropdownMenuBox(
                expanded = expandedRegion,
                onExpandedChange = { expandedRegion = !expandedRegion },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Región") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRegion,
                    onDismissRequest = { expandedRegion = false }
                ) {
                    RegionesChile.regiones.forEach { regionItem ->
                        DropdownMenuItem(
                            text = { Text(regionItem) },
                            onClick = {
                                region = regionItem
                                comuna = ""
                                expandedRegion = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // --- SELECTOR DE COMUNA (CORREGIDO) ---
            ExposedDropdownMenuBox(
                expanded = expandedComuna,
                onExpandedChange = { expandedComuna = !expandedComuna },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = comuna,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Comuna") },
                    placeholder = { Text(if (region.isEmpty()) "Seleccione Región primero" else "Seleccione Comuna") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedComuna) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, region.isNotEmpty())
                        .fillMaxWidth(),
                    enabled = region.isNotEmpty()
                )
                ExposedDropdownMenu(
                    expanded = expandedComuna,
                    onDismissRequest = { expandedComuna = false }
                ) {
                    if (comunasDisponibles.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay comunas disponibles") }, onClick = {})
                    } else {
                        comunasDisponibles.forEach { comunaItem ->
                            DropdownMenuItem(
                                text = { Text(comunaItem) },
                                onClick = {
                                    comuna = comunaItem
                                    expandedComuna = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Campos restantes (Dirección, Email, Password, Botones - sin cambios)
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección (Calle y Número)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = {
                    val error = validarCampos(
                        nombre = nombre,
                        apellido = apellido,
                        rut = rut,
                        region = region,
                        comuna = comuna,
                        direccion = direccion,
                        email = email,
                        password = password
                    )

                    if (error != null) {
                        viewModel.mensaje.value = error
                    } else {
                        viewModel.registrar(
                            nombre = nombre,
                            apellido = apellido,
                            rut = rut,
                            region = region,
                            comuna = comuna,
                            direccion = direccion,
                            email = email,
                            pass = password
                        )
                        navController.navigate("login")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }

            if (viewModel.mensaje.value.isNotEmpty()) {
                Text(
                    text = viewModel.mensaje.value,
                    color = if (viewModel.mensaje.value == "Registro exitoso ") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
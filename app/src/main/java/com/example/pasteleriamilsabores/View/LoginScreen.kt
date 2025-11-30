package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.pasteleriamilsabores.Destinos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCalido)
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la Pastelería Mil Sabores",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(120.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Inicio de Sesión",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Principal "Entrar"
            Button(
                onClick = {
                    // LÓGICA DE LOGIN Y REDIRECCIÓN
                    val usuarioLogueado = viewModel.login(email, password)

                    if (usuarioLogueado != null) {
                        // Verificación de dominio para Backoffice
                        // Si el correo termina en @milsabores.cl -> Es empleado/admin
                        if (email.endsWith("@milsabores.cl")) {
                            navController.navigate(Destinos.BACKOFFICE_BASE) {
                                popUpTo(Destinos.REGISTER_SCREEN) { inclusive = true }
                            }
                        } else {
                            // Cualquier otro dominio -> Es cliente -> Va al Home
                            navController.navigate("home/$email") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Entrar")
            }

            // Mensaje de feedback
            if (viewModel.mensaje.value.isNotEmpty()) {
                Text(
                    viewModel.mensaje.value,
                    modifier = Modifier.padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            TextButton(onClick = { navController.navigate(Destinos.REGISTER_SCREEN) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
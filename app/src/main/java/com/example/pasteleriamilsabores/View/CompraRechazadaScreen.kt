package com.example.pasteleriamilsabores.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriamilsabores.R // Importa tu R para los drawables

// Tus colores (o importa los de tu Theme)
val LightOrangeBackground = Color(0xFFFDECDD)
val DarkRedButton = Color(0xFFD9534F)
val DarkBrownText = Color(0xFF4E342E)

@Composable
fun CompraRechazadaScreen(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightOrangeBackground // Color de fondo de la pantalla
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite scroll si el contenido no cabe
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Alinea al top para el logo
        ) {

            // 1. Logo de la Pastelería
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate que 'logo' exista en res/drawable
                contentDescription = "Logo Pastelería Mil Sabores",
                modifier = Modifier
                    .fillMaxWidth(0.5f) // El logo ocupa el 50% del ancho
                    .padding(top = 32.dp, bottom = 64.dp)
            )

            // 2. Título "¡Pago Rechazado!"
            Text(
                text = "¡Pago Rechazado!",
                style = MaterialTheme.typography.headlineLarge, // Tipografía grande
                fontWeight = FontWeight.Bold,
                color = DarkBrownText,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 3. Icono de Cupcake Triste
            // --- USA TU ICONO PERSONALIZADO AQUÍ ---
            // Si tienes tu icono (ej. ic_pago_rechazado) en res/drawable:
            /*
            Image(
                painter = painterResource(id = R.drawable.ic_pago_rechazado),
                contentDescription = "Pago Rechazado",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            */

            // --- PLACEHOLDER (borra esto cuando tengas tu icono) ---
            Icon(
                imageVector = Icons.Filled.ReportProblem,
                contentDescription = "Pago Rechazado",
                tint = DarkRedButton, // Tinta el ícono de rojo
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )
            // --- Fin del Placeholder ---


            // 4. Mensaje de error
            Text(
                text = "Hubo un problema al procesar tu pago.\nPor favor, verifica tu información o intenta con otro método.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = DarkBrownText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Botón "Intentar de Nuevo"
            Button(
                onClick = {
                    // Vuelve a la pantalla anterior (ej. la pantalla de pago)
                    navController.popBackStack()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkRedButton),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Intentar de Nuevo", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}

// Preview para ver tu diseño en Android Studio
@Preview(showBackground = true)
@Composable
fun CompraRechazadaScreenPreview() {
    // Usamos un NavController "falso" para el preview
    CompraRechazadaScreen(navController = rememberNavController())
}
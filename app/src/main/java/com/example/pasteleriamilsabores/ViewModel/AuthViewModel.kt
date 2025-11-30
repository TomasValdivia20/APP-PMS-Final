package com.example.pasteleriamilsabores.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.Usuario
import com.example.pasteleriamilsabores.network.LoginRequest
import com.example.pasteleriamilsabores.network.RegisterRequestDto
import com.example.pasteleriamilsabores.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var mensaje = mutableStateOf("")

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    //  LOGIN
    fun login(email: String, pass: String, onResult: (Usuario?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()
                    _usuarioActual.value = user
                    mensaje.value = "Bienvenido, ${user?.nombre}"
                    onResult(user) // Éxito
                } else {
                    mensaje.value = "Credenciales incorrectas"
                    onResult(null) // Fallo
                }
            } catch (e: Exception) {
                mensaje.value = "Error de conexión"
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    // REGISTRO
    fun registrar(
        nombre: String, apellido: String, rut: String,
        region: String, comuna: String, direccion: String,
        email: String, pass: String,
        onSuccess: () -> Unit // Callback para navegar si es exitoso
    ) {
        viewModelScope.launch {
            try {
                val request = RegisterRequestDto(nombre, apellido, rut, region, comuna, direccion, email, pass)
                val response = RetrofitClient.instance.register(request)

                if (response.isSuccessful) {
                    mensaje.value = "Registro exitoso"
                    onSuccess()
                } else {
                    mensaje.value = response.errorBody()?.string() ?: "Error al registrar"
                }
            } catch (e: Exception) {
                mensaje.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }
}
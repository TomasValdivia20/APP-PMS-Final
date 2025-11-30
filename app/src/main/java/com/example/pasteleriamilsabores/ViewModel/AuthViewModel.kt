package com.example.pasteleriamilsabores.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.FakeDatabase
import com.example.pasteleriamilsabores.Model.Usuario
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    var mensaje = mutableStateOf("")
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    fun login(email: String, pass: String): Usuario? {
        val user = FakeDatabase.usuarios.find { it.email == email && it.password == pass }

        if (user != null) {
            mensaje.value = "Bienvenido, ${user.nombre}"
            _usuarioActual.value = user
            return user
        } else {
            mensaje.value = "Credenciales incorrectas"
            return null
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        rut: String,
        region: String,
        comuna: String,
        direccion: String,
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            val existe = FakeDatabase.usuarios.any { it.email == email }
            if (existe) {
                mensaje.value = "El correo ya está registrado"
            } else {
                val nuevoUsuario = Usuario(
                    id = (FakeDatabase.usuarios.size + 1),
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = pass,
                    direccion = direccion,
                    rut = rut,
                    region = region,
                    comuna = comuna
                )
                FakeDatabase.usuarios.add(nuevoUsuario)
                mensaje.value = "Registro exitoso"
            }
        }
    }

    fun register(email: String, pass: String, confirmPass: String): Boolean {
        if (pass != confirmPass) {
            mensaje.value = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
}
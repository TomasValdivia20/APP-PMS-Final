package com.example.pasteleriamilsabores.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.FakeDatabase
import com.example.pasteleriamilsabores.Model.Usuario
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var mensaje = mutableStateOf("")

    fun login(email: String, pass: String): Usuario? {
        val user = FakeDatabase.usuarios.find { it.email == email && it.password == pass }

        return if (user != null) {
            mensaje.value = "Bienvenido, ${user.nombre}"
            user // Retornamos el usuario encontrado
        } else {
            mensaje.value = "Credenciales incorrectas"
            null // Retornamos null si falló
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
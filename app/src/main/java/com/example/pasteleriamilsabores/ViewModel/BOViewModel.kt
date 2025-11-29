package com.example.pasteleriamilsabores.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Destinos
import com.example.pasteleriamilsabores.Model.*
import com.example.pasteleriamilsabores.Utils.FileUtils
import com.example.pasteleriamilsabores.network.RetrofitClient
import com.example.pasteleriamilsabores.repository.ProductoRepository
import com.example.pasteleriamilsabores.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class BOViewModel(
    private val context: Context,
    private val productoRepo: ProductoRepository = ProductoRepository(),
    private val categoriaRepo: CategoriaRepository = CategoriaRepository()
) : ViewModel() {

    // --- ESTADOS ---
    private val _ventas = MutableStateFlow<List<Venta>>(FakeBackofficeData.ventasRecientes)
    val ventas: StateFlow<List<Venta>> = _ventas
    val ventas15Dias = FakeBackofficeData.ventas15Dias
    val ventasSemestre = FakeBackofficeData.ventasSemestre

    private val _usuario = MutableStateFlow(FakeBackofficeData.usuarioActual)
    val usuario: StateFlow<UsuarioBackoffice> = _usuario

    private val _currentScreen = MutableStateFlow(Destinos.BODASHBOARD)
    val currentScreen: StateFlow<String> = _currentScreen

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _usuarios = MutableStateFlow<List<UsuarioBackoffice>>(FakeBackofficeData.usuariosFicticios)
    val usuarios: StateFlow<List<UsuarioBackoffice>> = _usuarios

    // Estado para feedback de operaciones
    val mensajeOperacion = MutableStateFlow<String?>(null)

    init {
        cargarProductos()
        cargarCategorias()
    }

    // --- Funcion para subir imagen ----
    fun subirImagen(uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val file = FileUtils.getFileFromUri(context, uri)
                if (file != null) {
                    // Preparar el archivo para Retrofit
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    // Llamada a la API
                    val response = RetrofitClient.instance.subirImagen(body)
                    val urlRelativa = response["url"]

                    if (urlRelativa != null) {
                        // Devolvemos la URL (ej: "uploads/foto.jpg") al callback
                        onSuccess(urlRelativa)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mensajeOperacion.value = "Error al subir imagen"
            }
        }
    }

    // --- CARGA DE DATOS ---
    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _productos.value = productoRepo.obtenerTodosLosProductos(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                // 🛑 CORRECCIÓN: Usamos 'obtenerCategorias' (la función que conecta al Backend)
                // en lugar de 'obtenerCategoriasDesdeAssets' (que devolvía vacío).
                _categorias.value = categoriaRepo.obtenerCategorias(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- CRUD PRODUCTOS ---
    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            val exito = productoRepo.crearProducto(producto)
            if (exito) {
                mensajeOperacion.value = "Producto creado con éxito"
                cargarProductos()
            } else {
                mensajeOperacion.value = "Error al crear producto"
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                // Nota: RetrofitClient debe tener el método PUT implementado en ApiService
                RetrofitClient.instance.actualizarProducto(producto.id, producto)
                mensajeOperacion.value = "Producto actualizado"
                cargarProductos()
            } catch (e: Exception) {
                e.printStackTrace()
                mensajeOperacion.value = "Error al actualizar"
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            if (productoRepo.eliminarProducto(id)) {
                mensajeOperacion.value = "Producto eliminado"
                cargarProductos()
            }
        }
    }

    // --- CRUD CATEGORÍAS ---
    fun crearCategoria(categoria: Categoria) {
        viewModelScope.launch {
            if (categoriaRepo.crearCategoria(categoria)) {
                mensajeOperacion.value = "Categoría creada"
                cargarCategorias()
            }
        }
    }

    fun actualizarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.actualizarCategoria(categoria.id, categoria)
                mensajeOperacion.value = "Categoría actualizada"
                cargarCategorias()
            } catch (e: Exception) {
                e.printStackTrace()
                mensajeOperacion.value = "Error al actualizar"
            }
        }
    }

    fun eliminarCategoria(id: Int) {
        viewModelScope.launch {
            if (categoriaRepo.eliminarCategoria(id)) {
                mensajeOperacion.value = "Categoría eliminada"
                cargarCategorias()
            }
        }
    }

    // --- NAVEGACIÓN ---
    fun navigateTo(route: String) {
        _currentScreen.value = route
    }

    // --- SIMULACIONES ---
    fun simularAgregarProducto() { println("SIMULACIÓN: Agregando un nuevo producto.") }
    fun simularAgregarCategoria() { println("SIMULACIÓN: Agregando una nueva categoría.") }
    fun simularActualizarPerfil(nuevoNombre: String, nuevoCorreo: String) {
        println("SIMULACIÓN: Perfil actualizado.")
        _usuario.value = _usuario.value.copy(nombre = nuevoNombre, correo = nuevoCorreo)
    }
}

// Factory (Sin cambios)
class BOViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BOViewModel::class.java)) {
            return BOViewModel(context.applicationContext, ProductoRepository(), CategoriaRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for BOViewModel")
    }
}
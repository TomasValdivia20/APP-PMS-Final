package com.example.pasteleriamilsabores.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Destinos
import com.example.pasteleriamilsabores.Model.*
import com.example.pasteleriamilsabores.repository.ProductoRepository
import com.example.pasteleriamilsabores.repository.CategoriaRepository
import com.example.pasteleriamilsabores.network.RetrofitClient
import com.example.pasteleriamilsabores.Utils.FileUtils
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

    // --- ESTADOS DE DATOS REALES (API) ---

    // 1. Usuarios Reales
    private val _usuariosReales = MutableStateFlow<List<UsuarioBackoffice>>(emptyList())
    val usuariosReales: StateFlow<List<UsuarioBackoffice>> = _usuariosReales

    // 2. Órdenes Reales
    private val _ordenesReales = MutableStateFlow<List<OrdenResponse>>(emptyList())
    val ordenesReales: StateFlow<List<OrdenResponse>> = _ordenesReales

    // 3. Reportes Reales
    private val _reporteVentas = MutableStateFlow<ReporteVentas?>(null)
    val reporteVentas: StateFlow<ReporteVentas?> = _reporteVentas

    // --- ESTADOS EXISTENTES ---
    // (Mantenemos estos para compatibilidad, pero ya no deberían usarse en la UI nueva)
    private val _ventas = MutableStateFlow<List<Venta>>(FakeBackofficeData.ventasRecientes)
    val ventas: StateFlow<List<Venta>> = _ventas
    val ventas15Dias = FakeBackofficeData.ventas15Dias
    val ventasSemestre = FakeBackofficeData.ventasSemestre



    private val _currentScreen = MutableStateFlow(Destinos.BODASHBOARD)
    val currentScreen: StateFlow<String> = _currentScreen

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    val mensajeOperacion = MutableStateFlow<String?>(null)

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        cargarProductos()
        cargarCategorias()
        cargarUsuarios()
        cargarOrdenes()
        cargarReportes()
    }

    // --- FUNCIONES DE CARGA (API) ---

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                _usuariosReales.value = RetrofitClient.instance.obtenerUsuarios()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarOrdenes() {
        viewModelScope.launch {
            try {
                _ordenesReales.value = RetrofitClient.instance.obtenerOrdenes()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarReportes() {
        viewModelScope.launch {
            try {
                _reporteVentas.value = RetrofitClient.instance.obtenerReporteVentas()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarProductos() {
        viewModelScope.launch {
            try { _productos.value = productoRepo.obtenerTodosLosProductos(context) }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try { _categorias.value = categoriaRepo.obtenerCategorias(context) }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- CRUD Y DEMÁS FUNCIONES (Igual que antes) ---
    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            val exito = productoRepo.crearProducto(producto)
            if (exito) {
                mensajeOperacion.value = "Producto creado"
                cargarProductos()
            } else { mensajeOperacion.value = "Error al crear" }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.actualizarProducto(producto.id, producto)
                mensajeOperacion.value = "Producto actualizado"
                cargarProductos()
            } catch (e: Exception) {
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

    fun subirImagen(uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val file = FileUtils.getFileFromUri(context, uri)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    val response = RetrofitClient.instance.subirImagen(body)
                    response["url"]?.let { onSuccess(it) }
                }
            } catch (e: Exception) {
                mensajeOperacion.value = "Error al subir imagen"
            }
        }
    }

    fun navigateTo(route: String) {
        _currentScreen.value = route
        // Recargar datos al navegar para asegurar que estén frescos
        if (route == Destinos.BODASHBOARD || route == Destinos.BOORDENES) cargarOrdenes()
        if (route == Destinos.BOUSUARIO) cargarUsuarios()
        if (route == Destinos.BOREPORTES || route == Destinos.BODASHBOARD) cargarReportes()
    }



}

class BOViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BOViewModel::class.java)) {
            return BOViewModel(context.applicationContext, ProductoRepository(), CategoriaRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for BOViewModel")
    }
}
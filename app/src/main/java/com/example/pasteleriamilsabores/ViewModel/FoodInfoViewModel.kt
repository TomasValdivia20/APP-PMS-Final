package com.example.pasteleriamilsabores.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.Model.FdcFood
import com.example.pasteleriamilsabores.network.FoodDataClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodInfoViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FdcFood>>(emptyList())
    val searchResults: StateFlow<List<FdcFood>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun buscarAlimento(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = FoodDataClient.service.searchFoods(
                    apiKey = FoodDataClient.API_KEY,
                    query = query
                )
                _searchResults.value = response.foods
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Error al buscar: ${e.localizedMessage}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarResultados() {
        _searchResults.value = emptyList()
        _errorMessage.value = null
    }
}
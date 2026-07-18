package com.delacruz.mibolsilloapp.feature.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.core.preferences.PerfilPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val perfilPreferences: PerfilPreferences,
) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    init {
        viewModelScope.launch {
            _nombre.value = perfilPreferences.obtenerNombre() ?: ""
        }
    }

    fun actualizarNombre(valor: String) {
        _nombre.value = valor
        _guardado.value = false
    }

    fun guardar() {
        viewModelScope.launch {
            perfilPreferences.guardarNombre(_nombre.value.trim())
            _guardado.value = true
        }
    }
}

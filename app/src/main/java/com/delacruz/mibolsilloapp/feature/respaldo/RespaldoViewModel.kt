package com.delacruz.mibolsilloapp.feature.respaldo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.core.backup.RespaldoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoRespaldo {
    data object Inactivo : EstadoRespaldo
    data object Procesando : EstadoRespaldo
    data object ExportacionExitosa : EstadoRespaldo
    data object RestauracionExitosa : EstadoRespaldo
    data class Error(val mensaje: String) : EstadoRespaldo
}

@HiltViewModel
class RespaldoViewModel @Inject constructor(
    private val respaldoManager: RespaldoManager,
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoRespaldo>(EstadoRespaldo.Inactivo)
    val estado: StateFlow<EstadoRespaldo> = _estado.asStateFlow()

    fun exportar(destino: Uri) {
        _estado.value = EstadoRespaldo.Procesando
        viewModelScope.launch {
            respaldoManager.exportarA(destino).fold(
                onSuccess = { _estado.value = EstadoRespaldo.ExportacionExitosa },
                onFailure = { e -> _estado.value = EstadoRespaldo.Error(e.message ?: "No se pudo exportar") },
            )
        }
    }

    fun restaurar(origen: Uri) {
        _estado.value = EstadoRespaldo.Procesando
        viewModelScope.launch {
            respaldoManager.restaurarDesde(origen).fold(
                onSuccess = { _estado.value = EstadoRespaldo.RestauracionExitosa },
                onFailure = { e -> _estado.value = EstadoRespaldo.Error(e.message ?: "No se pudo restaurar") },
            )
        }
    }
}

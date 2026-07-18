package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.CompromisoConSaldo
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CompromisosListViewModel @Inject constructor(
    private val repository: CompromisoRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    val compromisos: StateFlow<List<CompromisoConSaldo>> = repository.observarTodosConSaldo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun crear(nombre: String, montoTotal: BigDecimal, cuotasTotales: Int, diaPagoSugerido: Int) {
        viewModelScope.launch {
            repository.crear(
                Compromiso(
                    nombre = nombre,
                    montoTotal = montoTotal,
                    cuotasTotales = cuotasTotales,
                    diaPagoSugerido = diaPagoSugerido,
                    estado = EstadoCompromiso.ACTIVO,
                ),
            )
        }
    }

    fun actualizar(compromiso: Compromiso) {
        viewModelScope.launch { repository.actualizar(compromiso) }
    }

    fun eliminar(compromiso: Compromiso) {
        viewModelScope.launch { repository.eliminar(compromiso) }
    }
}

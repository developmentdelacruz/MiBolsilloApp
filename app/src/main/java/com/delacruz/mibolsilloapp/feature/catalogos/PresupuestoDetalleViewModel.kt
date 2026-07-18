package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.GastoMensual
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val MESES_HISTORIAL = 12

@HiltViewModel
class PresupuestoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    presupuestoRepository: PresupuestoRepository,
    transaccionRepository: TransaccionRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    private val categoriaId: Long = checkNotNull(savedStateHandle["categoriaId"])

    val presupuesto: StateFlow<PresupuestoConConsumo?> = presupuestoRepository.observarTodosConConsumo()
        .map { lista -> lista.find { it.presupuesto.categoriaId == categoriaId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val historial: StateFlow<List<GastoMensual>> =
        transaccionRepository.observarGastoMensualPorCategoria(categoriaId, MESES_HISTORIAL)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
}

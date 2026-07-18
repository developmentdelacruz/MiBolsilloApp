package com.delacruz.mibolsilloapp.feature.gastoscompartidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import com.delacruz.mibolsilloapp.domain.model.GastoCompartido
import com.delacruz.mibolsilloapp.domain.model.GastoCompartidoConTransaccion
import com.delacruz.mibolsilloapp.domain.repository.GastoCompartidoRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class GastosCompartidosViewModel @Inject constructor(
    private val repository: GastoCompartidoRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    val gastos: StateFlow<List<GastoCompartidoConTransaccion>> = repository.observarTodosConTransaccion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun marcarPagado(gasto: GastoCompartido) {
        viewModelScope.launch { repository.actualizar(gasto.copy(estadoPago = EstadoPago.PAGADO)) }
    }
}

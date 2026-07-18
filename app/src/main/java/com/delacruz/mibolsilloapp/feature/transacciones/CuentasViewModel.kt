package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.CuentaConSaldo
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.model.TipoCuenta
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
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
class CuentasViewModel @Inject constructor(
    private val repository: CuentaRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    val cuentasConSaldo: StateFlow<List<CuentaConSaldo>> = repository.observarTodasConSaldo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val monedas: StateFlow<List<Moneda>> = monedaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun crear(
        nombre: String,
        tipo: TipoCuenta,
        monedaId: Long,
        saldoInicial: BigDecimal,
        limiteCredito: BigDecimal? = null,
    ) {
        viewModelScope.launch {
            repository.crear(
                Cuenta(
                    nombre = nombre,
                    tipo = tipo,
                    monedaId = monedaId,
                    saldoInicial = saldoInicial,
                    limiteCredito = limiteCredito,
                ),
            )
        }
    }

    fun actualizar(cuenta: Cuenta) {
        viewModelScope.launch { repository.actualizar(cuenta) }
    }

    fun eliminar(cuenta: Cuenta) {
        viewModelScope.launch { repository.eliminar(cuenta) }
    }
}

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TransaccionesViewModel @Inject constructor(
    private val repository: TransaccionRepository,
    categoriaRepository: CategoriaRepository,
    negocioRepository: NegocioRepository,
    proyectoRepository: ProyectoRepository,
    cuentaRepository: CuentaRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val transacciones: StateFlow<List<Transaccion>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val negocios: StateFlow<List<Negocio>> = negocioRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val proyectos: StateFlow<List<Proyecto>> = proyectoRepository.observarTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cuentas: StateFlow<List<Cuenta>> = cuentaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val balanceTotal: StateFlow<BigDecimal> = cuentaRepository.observarSaldoTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BigDecimal.ZERO)

    fun crear(
        descripcion: String,
        monto: BigDecimal,
        fecha: LocalDate,
        tipo: TipoTransaccion,
        categoriaId: Long,
        cuentaId: Long,
        negocioId: Long?,
        proyectoId: Long?,
    ) {
        viewModelScope.launch {
            repository.crear(
                Transaccion(
                    descripcion = descripcion,
                    monto = monto,
                    fecha = fecha,
                    tipo = tipo,
                    categoriaId = categoriaId,
                    cuentaId = cuentaId,
                    negocioId = negocioId,
                    proyectoId = proyectoId,
                ),
            )
        }
    }

    fun eliminar(transaccion: Transaccion) {
        viewModelScope.launch { repository.eliminar(transaccion) }
    }
}

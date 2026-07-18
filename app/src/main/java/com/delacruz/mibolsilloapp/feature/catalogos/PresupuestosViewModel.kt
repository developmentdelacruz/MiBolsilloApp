package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import com.delacruz.mibolsilloapp.domain.usecase.CalcularRolloverPresupuestoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PresupuestosViewModel @Inject constructor(
    private val repository: PresupuestoRepository,
    categoriaRepository: CategoriaRepository,
    monedaRepository: MonedaRepository,
    private val calcularRolloverUseCase: CalcularRolloverPresupuestoUseCase,
) : ViewModel() {

    val presupuestos: StateFlow<List<PresupuestoConConsumo>> = repository.observarTodosConConsumo()
        .map { lista ->
            lista.map { item -> item.copy(rolloverAcumulado = calcularRolloverUseCase(item.presupuesto)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    /** Categorías de gasto que todavía no tienen un presupuesto asignado (evita el UNIQUE de categoriaId). */
    val categoriasDisponibles: StateFlow<List<Categoria>> = combine(
        categoriaRepository.observarTodas(),
        presupuestos,
    ) { categorias, presupuestosActuales ->
        val categoriasConPresupuesto = presupuestosActuales.map { it.presupuesto.categoriaId }.toSet()
        categorias.filter { it.tipo == TipoCategoria.GASTO && it.id !in categoriasConPresupuesto }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crear(categoriaId: Long, montoMensual: BigDecimal) {
        viewModelScope.launch {
            repository.crear(Presupuesto(categoriaId = categoriaId, montoMensual = montoMensual))
        }
    }

    fun actualizar(presupuesto: Presupuesto) {
        viewModelScope.launch { repository.actualizar(presupuesto) }
    }

    fun eliminar(presupuesto: Presupuesto) {
        viewModelScope.launch { repository.eliminar(presupuesto) }
    }
}

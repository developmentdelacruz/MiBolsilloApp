package com.delacruz.mibolsilloapp.feature.suscripciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SuscripcionesListViewModel @Inject constructor(
    private val repository: SuscripcionRepository,
    categoriaRepository: CategoriaRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    val suscripciones: StateFlow<List<Suscripcion>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun crear(nombre: String, montoMensual: BigDecimal, diaCobro: Int, categoriaId: Long) {
        viewModelScope.launch {
            repository.crear(
                Suscripcion(nombre = nombre, montoMensual = montoMensual, diaCobro = diaCobro, categoriaId = categoriaId),
            )
        }
    }

    fun actualizar(suscripcion: Suscripcion) {
        viewModelScope.launch { repository.actualizar(suscripcion) }
    }

    fun eliminar(suscripcion: Suscripcion) {
        viewModelScope.launch { repository.eliminar(suscripcion) }
    }
}

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConCosto
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProyectosViewModel @Inject constructor(
    private val repository: ProyectoRepository,
) : ViewModel() {

    val proyectos: StateFlow<List<ProyectoConCosto>> = repository.observarTodosConCosto()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crear(nombre: String, presupuestoEstimado: BigDecimal) {
        viewModelScope.launch {
            repository.crear(Proyecto(nombre = nombre, presupuestoEstimado = presupuestoEstimado))
        }
    }
}

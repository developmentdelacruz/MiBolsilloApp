package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.ProyectoConCosto
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProyectoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    proyectoRepository: ProyectoRepository,
    transaccionRepository: TransaccionRepository,
) : ViewModel() {

    private val proyectoId: Long = checkNotNull(savedStateHandle["proyectoId"])

    val proyecto: StateFlow<ProyectoConCosto?> = proyectoRepository.observarConCosto(proyectoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val transacciones: StateFlow<List<Transaccion>> = transaccionRepository.observarPorProyecto(proyectoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

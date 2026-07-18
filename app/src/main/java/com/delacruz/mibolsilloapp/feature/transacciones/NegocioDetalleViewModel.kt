package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class NegocioDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    negocioRepository: NegocioRepository,
    transaccionRepository: TransaccionRepository,
    monedaRepository: MonedaRepository,
) : ViewModel() {

    private val negocioId: Long = checkNotNull(savedStateHandle["negocioId"])

    val negocio = negocioRepository.observarPorId(negocioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val transacciones: StateFlow<List<Transaccion>> = transaccionRepository.observarPorNegocio(negocioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val simboloMoneda: StateFlow<String> = monedaRepository.observarPredeterminada()
        .map { it?.simbolo ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
}

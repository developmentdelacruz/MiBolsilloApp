package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.CompromisoConPagos
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.usecase.RegistrarPagoCompromisoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CompromisoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: CompromisoRepository,
    private val registrarPagoUseCase: RegistrarPagoCompromisoUseCase,
) : ViewModel() {

    private val compromisoId: Long = checkNotNull(savedStateHandle["compromisoId"])

    val detalle: StateFlow<CompromisoConPagos?> = repository.observarConPagos(compromisoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun registrarPago(monto: BigDecimal, fecha: LocalDate, numeroCuota: Int, esAdelantado: Boolean) {
        viewModelScope.launch {
            registrarPagoUseCase(
                PagoCompromiso(
                    compromisoId = compromisoId,
                    fechaPagoReal = fecha,
                    montoPagado = monto,
                    numeroCuota = numeroCuota,
                    esAdelantado = esAdelantado,
                ),
            )
        }
    }
}

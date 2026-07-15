package com.delacruz.mibolsilloapp.feature.suscripciones

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import com.delacruz.mibolsilloapp.domain.model.SuscripcionCompartida
import com.delacruz.mibolsilloapp.domain.model.SuscripcionConInvitados
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import java.math.BigDecimal
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SuscripcionDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SuscripcionRepository,
) : ViewModel() {

    private val suscripcionId: Long = checkNotNull(savedStateHandle["suscripcionId"])

    val detalle: StateFlow<SuscripcionConInvitados?> = repository.observarConInvitados(suscripcionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun agregarInvitado(nombreContacto: String, telefono: String, montoAPagar: BigDecimal) {
        viewModelScope.launch {
            repository.agregarInvitado(
                SuscripcionCompartida(
                    suscripcionId = suscripcionId,
                    nombreContacto = nombreContacto,
                    telefono = telefono,
                    montoAPagar = montoAPagar,
                ),
            )
        }
    }

    fun marcarPagado(invitado: SuscripcionCompartida) {
        viewModelScope.launch {
            repository.actualizarInvitado(invitado.copy(estadoPago = EstadoPago.PAGADO))
        }
    }
}

package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class GastoCompartido(
    val id: Long = 0,
    val transaccionId: Long,
    val nombreContacto: String,
    val telefono: String,
    val montoAPagar: BigDecimal,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
)

data class GastoCompartidoConTransaccion(
    val gasto: GastoCompartido,
    val descripcionTransaccion: String,
    val fechaTransaccion: LocalDate,
)

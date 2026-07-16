package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class Suscripcion(
    val id: Long = 0,
    val nombre: String,
    val montoMensual: BigDecimal,
    val diaCobro: Int,
    val categoriaId: Long,
)

data class SuscripcionCompartida(
    val id: Long = 0,
    val suscripcionId: Long,
    val nombreContacto: String,
    val telefono: String,
    val montoAPagar: BigDecimal,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
)

enum class EstadoPago {
    PENDIENTE,
    PAGADO,
}

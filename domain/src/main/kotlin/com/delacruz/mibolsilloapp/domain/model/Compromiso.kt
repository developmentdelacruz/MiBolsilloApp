package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Compromiso(
    val id: Long = 0,
    val nombre: String,
    val montoTotal: BigDecimal,
    val cuotasTotales: Int,
    val diaPagoSugerido: Int,
    val estado: EstadoCompromiso,
)

data class PagoCompromiso(
    val id: Long = 0,
    val compromisoId: Long,
    val fechaPagoReal: LocalDate,
    val montoPagado: BigDecimal,
    val numeroCuota: Int,
    val esAdelantado: Boolean = false,
)

enum class EstadoCompromiso {
    ACTIVO,
    FINALIZADO,
}

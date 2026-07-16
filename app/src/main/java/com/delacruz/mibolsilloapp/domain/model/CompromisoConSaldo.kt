package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class CompromisoConSaldo(
    val compromiso: Compromiso,
    val saldoPendiente: BigDecimal,
    val cuotasPagadas: Int,
)

data class CompromisoConPagos(
    val compromiso: Compromiso,
    val pagos: List<PagoCompromiso>,
)

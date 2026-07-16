package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class Cuenta(
    val id: Long = 0,
    val nombre: String,
    val tipo: TipoCuenta,
    val monedaId: Long,
    val saldoInicial: BigDecimal,
    val activa: Boolean = true,
)

enum class TipoCuenta {
    EFECTIVO,
    BANCO,
    TARJETA,
}

data class CuentaConSaldo(
    val cuenta: Cuenta,
    val saldoActual: BigDecimal,
)

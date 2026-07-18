package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class Cuenta(
    val id: Long = 0,
    val nombre: String,
    val tipo: TipoCuenta,
    val monedaId: Long,
    val saldoInicial: BigDecimal,
    val activa: Boolean = true,
    /** Solo relevante para TARJETA; habilita el % de utilización. */
    val limiteCredito: BigDecimal? = null,
) {
    /** Una tarjeta de crédito ES su propio pasivo: su deuda es directamente su saldo negativo. */
    val esPasivo: Boolean get() = tipo == TipoCuenta.TARJETA
}

enum class TipoCuenta {
    EFECTIVO,
    BANCO,
    TARJETA,
}

data class CuentaConSaldo(
    val cuenta: Cuenta,
    val saldoActual: BigDecimal,
) {
    val porcentajeUtilizado: Float? get() {
        val limite = cuenta.limiteCredito
        if (!cuenta.esPasivo || limite == null || limite.signum() <= 0) return null
        return (saldoActual.abs().toFloat() / limite.toFloat()).coerceIn(0f, 1f)
    }
}

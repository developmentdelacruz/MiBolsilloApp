package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class Presupuesto(
    val id: Long = 0,
    val categoriaId: Long,
    val montoMensual: BigDecimal,
)

/** Consumo del mes en curso para un presupuesto. Se recalcula en cada emisión, no se cachea. */
data class PresupuestoConConsumo(
    val presupuesto: Presupuesto,
    val categoria: Categoria,
    val consumido: BigDecimal,
) {
    val restante: BigDecimal get() = presupuesto.montoMensual - consumido

    val porcentajeConsumido: Float get() {
        if (presupuesto.montoMensual.signum() <= 0) return 0f
        return (consumido.toFloat() / presupuesto.montoMensual.toFloat()).coerceIn(0f, 1f)
    }
}

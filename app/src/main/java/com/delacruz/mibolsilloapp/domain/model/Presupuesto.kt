package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Presupuesto(
    val id: Long = 0,
    val categoriaId: Long,
    val montoMensual: BigDecimal,
    val creadoEn: LocalDate = LocalDate.now(),
    val activo: Boolean = true,
)

/** Consumo del mes en curso para un presupuesto. Se recalcula en cada emisión, no se cachea. */
data class PresupuestoConConsumo(
    val presupuesto: Presupuesto,
    val categoria: Categoria,
    val consumido: BigDecimal,
    /** Sobrante acumulado de meses anteriores (rollover estilo YNAB, con piso en 0 por mes). */
    val rolloverAcumulado: BigDecimal = BigDecimal.ZERO,
) {
    val restante: BigDecimal get() = presupuesto.montoMensual - consumido

    val disponibleTotal: BigDecimal get() = restante + rolloverAcumulado

    val porcentajeConsumido: Float get() {
        if (presupuesto.montoMensual.signum() <= 0) return 0f
        return (consumido.toFloat() / presupuesto.montoMensual.toFloat()).coerceIn(0f, 1f)
    }
}

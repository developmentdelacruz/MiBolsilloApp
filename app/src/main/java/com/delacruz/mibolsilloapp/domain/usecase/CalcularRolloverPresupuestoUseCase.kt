package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

/**
 * Rollover estilo YNAB: el sobrante no gastado de un mes se acumula para el siguiente.
 * Se calcula en memoria (no en SQL, evita CTEs recursivos en SQLite) iterando mes a mes
 * desde que el presupuesto fue creado hasta el mes anterior al actual.
 *
 * Simplificación deliberada: piso en 0 por mes. Un mes de sobregasto no resta al arrastre
 * acumulado (a diferencia del modelo "true expense" de YNAB, que sí permite arrastre
 * negativo) — más simple y menos confuso para uso solo. Se limita a 36 meses iterados
 * para evitar loops largos si un presupuesto lleva mucho tiempo activo.
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class CalcularRolloverPresupuestoUseCase(
    private val transaccionRepository: TransaccionRepository,
) {
    suspend operator fun invoke(presupuesto: Presupuesto, hasta: LocalDate = LocalDate.now()): BigDecimal {
        var rollover = BigDecimal.ZERO
        var mes = YearMonth.from(presupuesto.creadoEn)
        val mesActual = YearMonth.from(hasta)
        var mesesIterados = 0

        while (mes.isBefore(mesActual) && mesesIterados < MAX_MESES) {
            val gasto = transaccionRepository.gastoDeCategoriaEnMes(
                presupuesto.categoriaId,
                mes.year,
                mes.monthValue,
            )
            val sobranteDelMes = (presupuesto.montoMensual - gasto).coerceAtLeast(BigDecimal.ZERO)
            rollover += sobranteDelMes
            mes = mes.plusMonths(1)
            mesesIterados++
        }

        return rollover
    }

    private companion object {
        const val MAX_MESES = 36
    }
}

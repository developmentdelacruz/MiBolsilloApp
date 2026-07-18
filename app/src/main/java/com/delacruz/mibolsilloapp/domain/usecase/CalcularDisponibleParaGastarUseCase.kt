package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * "Safe to spend": el balance total menos lo ya comprometido (deudas activas y presupuesto
 * del mes aún no gastado), para no contar como "libre" dinero que ya tiene un destino.
 *
 * Usa el restante del mes actual de cada presupuesto SIN el ajuste de rollover (ver
 * CalcularRolloverPresupuestoUseCase) para no acoplar ambas features — el rollover es
 * informativo dentro de Presupuestos, no cambia cuánto es "seguro" gastar hoy.
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class CalcularDisponibleParaGastarUseCase(
    private val cuentaRepository: CuentaRepository,
    private val compromisoRepository: CompromisoRepository,
    private val presupuestoRepository: PresupuestoRepository,
) {
    operator fun invoke(): Flow<BigDecimal> = combine(
        cuentaRepository.observarSaldoTotal(),
        compromisoRepository.observarTotalSaldoPendiente(),
        presupuestoRepository.observarTodosConConsumo(),
    ) { balance, pendienteCompromisos, presupuestos ->
        val comprometidoPresupuestos = presupuestos
            .filter { it.presupuesto.activo }
            .fold(BigDecimal.ZERO) { acumulado, item -> acumulado + item.restante.coerceAtLeast(BigDecimal.ZERO) }
        balance - pendienteCompromisos - comprometidoPresupuestos
    }
}

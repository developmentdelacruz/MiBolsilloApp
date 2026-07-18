package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Patrimonio neto = Activos - Pasivos. No existe una entidad "Pasivo" separada:
 * una cuenta TARJETA ya ES su propio pasivo (su deuda es directamente su saldo negativo,
 * ver Cuenta.esPasivo), y la deuda externa (préstamos informales) ya la modela
 * Compromiso — se reusa observarTotalSaldoPendiente() creado para safe-to-spend.
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class CalcularPatrimonioNetoUseCase(
    private val cuentaRepository: CuentaRepository,
    private val compromisoRepository: CompromisoRepository,
) {
    operator fun invoke(): Flow<BigDecimal> = combine(
        cuentaRepository.observarTodasConSaldo(),
        compromisoRepository.observarTotalSaldoPendiente(),
    ) { cuentas, deudaExterna ->
        val (pasivos, activos) = cuentas.partition { it.cuenta.esPasivo }
        val totalActivos = activos.fold(BigDecimal.ZERO) { acumulado, item -> acumulado + item.saldoActual }
        val totalPasivosDeCuenta = pasivos.fold(BigDecimal.ZERO) { acumulado, item ->
            acumulado + item.saldoActual.abs()
        }
        totalActivos - totalPasivosDeCuenta - deudaExterna
    }
}

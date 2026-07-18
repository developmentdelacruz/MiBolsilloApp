package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.model.SugerenciaRecurrencia
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

/**
 * Detecta gastos que se repiten con patrón mensual (mismo negocio + categoría, montos
 * similares, cadencia ~30 días) para sugerir convertirlos en Compromiso o Suscripción.
 * Análisis puramente en memoria — no requiere esquema nuevo, y excluye cuotas de compra
 * (compraId != null) porque esas ya son recurrentes por diseño, no por patrón detectado.
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class DetectarTransaccionesRecurrentesUseCase(
    private val transaccionRepository: TransaccionRepository,
) {
    suspend operator fun invoke(): List<SugerenciaRecurrencia> {
        val transacciones = transaccionRepository.observarTodas().first()
            .filter { it.tipo == TipoTransaccion.GASTO && it.negocioId != null && it.compraId == null }

        return transacciones
            .groupBy { it.negocioId!! to it.categoriaId }
            .mapNotNull { (clave, lista) ->
                if (lista.size < MINIMO_OCURRENCIAS) return@mapNotNull null

                val fechasOrdenadas = lista.map { it.fecha }.sorted()
                val cadenciaMensual = fechasOrdenadas.zipWithNext().all { (anterior, siguiente) ->
                    ChronoUnit.DAYS.between(anterior, siguiente) in RANGO_DIAS_MENSUAL
                }
                if (!cadenciaMensual) return@mapNotNull null

                val montoPromedio = lista
                    .fold(BigDecimal.ZERO) { acumulado, t -> acumulado + t.monto }
                    .divide(lista.size.toBigDecimal(), 2, RoundingMode.HALF_UP)
                val toleranciaMaxima = montoPromedio * TOLERANCIA_MONTO
                val dentroDeTolerancia = lista.all { (it.monto - montoPromedio).abs() <= toleranciaMaxima }
                if (!dentroDeTolerancia) return@mapNotNull null

                SugerenciaRecurrencia(
                    negocioId = clave.first,
                    categoriaId = clave.second,
                    montoPromedio = montoPromedio,
                    ocurrencias = lista.size,
                    ultimaFecha = fechasOrdenadas.last(),
                )
            }
    }

    private companion object {
        const val MINIMO_OCURRENCIAS = 3
        val RANGO_DIAS_MENSUAL = 25L..35L
        val TOLERANCIA_MONTO = BigDecimal("0.1")
    }
}

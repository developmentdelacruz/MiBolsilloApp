package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.model.Compra
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import java.math.RoundingMode

/**
 * Crea la Compra y genera sus N cuotas como Transaccion (una por mes desde
 * fechaPrimeraCuota). El monto se reparte por división entera y el resto se ajusta en la
 * última cuota, para que la suma de las cuotas sea exactamente igual al monto total (sin
 * drift de redondeo).
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class GenerarCuotasCompraUseCase(
    private val compraRepository: CompraRepository,
    private val transaccionRepository: TransaccionRepository,
) {
    suspend operator fun invoke(compra: Compra): Long {
        val compraId = compraRepository.crear(compra)

        val montoPorCuota = compra.montoTotal.divide(compra.cuotasTotales.toBigDecimal(), 2, RoundingMode.DOWN)
        val montoUltimaCuota = compra.montoTotal - montoPorCuota.multiply((compra.cuotasTotales - 1).toBigDecimal())

        for (numero in 1..compra.cuotasTotales) {
            val monto = if (numero == compra.cuotasTotales) montoUltimaCuota else montoPorCuota
            transaccionRepository.crear(
                Transaccion(
                    descripcion = "${compra.descripcion} (cuota $numero/${compra.cuotasTotales})",
                    monto = monto,
                    fecha = compra.fechaPrimeraCuota.plusMonths((numero - 1).toLong()),
                    tipo = TipoTransaccion.GASTO,
                    categoriaId = compra.categoriaId,
                    cuentaId = compra.cuentaId,
                    negocioId = compra.negocioId,
                    compraId = compraId,
                    numeroCuota = numero,
                ),
            )
        }

        return compraId
    }
}

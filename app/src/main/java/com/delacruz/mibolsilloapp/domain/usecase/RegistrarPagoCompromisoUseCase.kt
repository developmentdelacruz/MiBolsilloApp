package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.first

/**
 * No es un simple passthrough al repositorio: aplica la regla de negocio de que un
 * Compromiso pasa a FINALIZADO automáticamente cuando su saldo pendiente llega a 0
 * (incluyendo abonos extraordinarios que adelanten el fin). Por eso amerita su propio
 * caso de uso y no vive directo en el ViewModel ni en el repositorio.
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class RegistrarPagoCompromisoUseCase(
    private val compromisoRepository: CompromisoRepository,
) {
    suspend operator fun invoke(pago: PagoCompromiso): Result<Unit> = runCatching {
        compromisoRepository.registrarPago(pago)

        val actualizado = compromisoRepository.observarConSaldo(pago.compromisoId).first()
            ?: error("Compromiso ${pago.compromisoId} no existe")

        if (actualizado.saldoPendiente <= BigDecimal.ZERO &&
            actualizado.compromiso.estado != EstadoCompromiso.FINALIZADO
        ) {
            compromisoRepository.actualizar(actualizado.compromiso.copy(estado = EstadoCompromiso.FINALIZADO))
        }
    }
}

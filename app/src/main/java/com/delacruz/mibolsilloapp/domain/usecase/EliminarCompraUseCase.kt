package com.delacruz.mibolsilloapp.domain.usecase

import com.delacruz.mibolsilloapp.domain.model.Compra
import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import java.time.LocalDate

/**
 * Cancela una compra en cuotas: las cuotas futuras (aún no ejecutadas) se borran, pero
 * las pasadas quedan como transacción normal porque ese dinero ya se movió de la cuenta —
 * borrarlas alteraría el historial real de gasto. Por eso no es un simple delete en
 * cascada (ver FK SET_NULL en TransaccionEntity.compraId).
 *
 * Clase sin @Inject a propósito: :domain no depende de ningún framework de DI.
 * El binding vive en data/di/UseCaseModule.kt.
 */
class EliminarCompraUseCase(
    private val compraRepository: CompraRepository,
    private val transaccionRepository: TransaccionRepository,
) {
    suspend operator fun invoke(compra: Compra, hoy: LocalDate = LocalDate.now()) {
        transaccionRepository.eliminarCuotasFuturasDeCompra(compra.id, hoy)
        transaccionRepository.desvincularCuotasDeCompra(compra.id)
        compraRepository.eliminarRegistro(compra)
    }
}

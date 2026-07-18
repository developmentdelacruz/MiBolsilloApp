package com.delacruz.mibolsilloapp.core.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifier
import com.delacruz.mibolsilloapp.core.preferences.PerfilPreferences
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

@HiltWorker
class PaymentReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val compromisoRepository: CompromisoRepository,
    private val suscripcionRepository: SuscripcionRepository,
    private val perfilPreferences: PerfilPreferences,
    private val notifier: PaymentNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hoy = LocalDate.now()
        val prefijo = perfilPreferences.obtenerNombre()?.let { "$it, " } ?: ""

        compromisoRepository.observarTodosConSaldo().first()
            .filter { it.compromiso.estado == EstadoCompromiso.ACTIVO && it.saldoPendiente > BigDecimal.ZERO }
            .forEach { item ->
                val proxima = proximaFechaDePago(item.compromiso.diaPagoSugerido, hoy)
                if (estaProximo(hoy, proxima)) {
                    notifier.notificarPagoProximo(
                        id = item.compromiso.id,
                        titulo = "${prefijo}Pago próximo: ${item.compromiso.nombre}",
                        mensaje = "Vence el $proxima. Saldo pendiente: ${item.saldoPendiente}",
                    )
                }
            }

        suscripcionRepository.observarTodas().first()
            .forEach { suscripcion ->
                val proxima = proximaFechaDePago(suscripcion.diaCobro, hoy)
                if (estaProximo(hoy, proxima)) {
                    notifier.notificarPagoProximo(
                        id = suscripcion.id + ID_OFFSET_SUSCRIPCION,
                        titulo = "${prefijo}Cobro próximo: ${suscripcion.nombre}",
                        mensaje = "Se cobra el $proxima: ${suscripcion.montoMensual}",
                    )
                }
            }

        return Result.success()
    }

    private fun estaProximo(hoy: LocalDate, fecha: LocalDate): Boolean =
        ChronoUnit.DAYS.between(hoy, fecha) in 0..DIAS_ANTICIPACION

    private companion object {
        const val DIAS_ANTICIPACION = 3L

        // Compromisos y Suscripciones usan Long autogenerado desde 1 en tablas separadas,
        // así que sus ids chocan; se desplaza el rango de Suscripciones para no pisar
        // la notificación de un Compromiso con el mismo id numérico.
        const val ID_OFFSET_SUSCRIPCION = 1_000_000L
    }
}

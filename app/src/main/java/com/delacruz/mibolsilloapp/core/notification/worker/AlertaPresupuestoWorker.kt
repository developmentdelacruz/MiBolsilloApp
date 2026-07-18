package com.delacruz.mibolsilloapp.core.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifier
import com.delacruz.mibolsilloapp.core.preferences.PerfilPreferences
import com.delacruz.mibolsilloapp.domain.model.NivelAlertaPresupuesto
import com.delacruz.mibolsilloapp.domain.repository.AlertaPresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.YearMonth
import kotlinx.coroutines.flow.first

@HiltWorker
class AlertaPresupuestoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val presupuestoRepository: PresupuestoRepository,
    private val alertaPresupuestoRepository: AlertaPresupuestoRepository,
    private val perfilPreferences: PerfilPreferences,
    private val notifier: PaymentNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val mesActual = YearMonth.now()
        val prefijo = perfilPreferences.obtenerNombre()?.let { "$it, " } ?: ""

        presupuestoRepository.observarTodosConConsumo().first()
            .filter { it.presupuesto.activo }
            .forEach { item ->
                val nivel = when {
                    item.porcentajeConsumido >= 1f -> NivelAlertaPresupuesto.CIEN
                    item.porcentajeConsumido >= UMBRAL_OCHENTA -> NivelAlertaPresupuesto.OCHENTA
                    else -> null
                } ?: return@forEach

                if (alertaPresupuestoRepository.yaSeAlerto(item.presupuesto.id, mesActual, nivel)) return@forEach

                val (titulo, mensaje) = when (nivel) {
                    NivelAlertaPresupuesto.CIEN -> "${prefijo}Te pasaste del presupuesto de ${item.categoria.nombre}" to
                        "Gastaste ${item.consumido} de ${item.presupuesto.montoMensual} presupuestados este mes."
                    NivelAlertaPresupuesto.OCHENTA -> "${prefijo}Estás cerca del límite de ${item.categoria.nombre}" to
                        "Ya gastaste ${item.consumido} de ${item.presupuesto.montoMensual} presupuestados este mes."
                }
                notifier.notificarPresupuestoExcedido(
                    id = item.presupuesto.id + ID_OFFSET_PRESUPUESTO,
                    titulo = titulo,
                    mensaje = mensaje,
                )
                alertaPresupuestoRepository.registrarAlerta(item.presupuesto.id, mesActual, nivel)
            }

        return Result.success()
    }

    private companion object {
        const val UMBRAL_OCHENTA = 0.8f

        // Compromisos usa ids sin offset y Suscripciones usa +1_000_000L (ver
        // PaymentReminderWorker) — se corre el rango de Presupuestos otro tanto más para que
        // ninguna notificación de las tres fuentes pise el id de otra.
        const val ID_OFFSET_PRESUPUESTO = 2_000_000L
    }
}

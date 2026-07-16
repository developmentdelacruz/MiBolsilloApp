package com.delacruz.mibolsilloapp.core.notification.notifier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PaymentNotifierImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PaymentNotifier {

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CANAL_PAGOS,
                "Recordatorios de pago",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                // La vibración es opt-in por canal: IMPORTANCE_DEFAULT por sí solo no vibra.
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun notificarPagoProximo(id: Long, titulo: String, mensaje: String) {
        val notification = NotificationCompat.Builder(context, CANAL_PAGOS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .build()

        // Requiere permiso POST_NOTIFICATIONS concedido en tiempo de ejecución (API 33+).
        // Si no está concedido, NotificationManagerCompat.notify simplemente no muestra nada.
        NotificationManagerCompat.from(context).notify(id.toInt(), notification)
    }

    private companion object {
        const val CANAL_PAGOS = "pagos_proximos"
    }
}

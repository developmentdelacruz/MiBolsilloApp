package com.delacruz.mibolsilloapp.core.notification.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.delacruz.mibolsilloapp.core.notification.worker.PaymentReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationScheduler {

    override fun programarVerificacionDiaria() {
        val request = PeriodicWorkRequestBuilder<PaymentReminderWorker>(Duration.ofHours(24))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOMBRE_TRABAJO,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    private companion object {
        const val NOMBRE_TRABAJO = "verificacion_pagos_diaria"
    }
}

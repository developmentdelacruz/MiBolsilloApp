package com.delacruz.mibolsilloapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.delacruz.mibolsilloapp.core.notification.scheduler.AlertaPresupuestoScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.NotificationScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.PatrimonioScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MiBolsilloApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var notificationScheduler: NotificationScheduler

    @Inject lateinit var patrimonioScheduler: PatrimonioScheduler

    @Inject lateinit var alertaPresupuestoScheduler: AlertaPresupuestoScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        notificationScheduler.programarVerificacionDiaria()
        patrimonioScheduler.programarSnapshotDiario()
        alertaPresupuestoScheduler.programarVerificacionDiaria()
    }
}

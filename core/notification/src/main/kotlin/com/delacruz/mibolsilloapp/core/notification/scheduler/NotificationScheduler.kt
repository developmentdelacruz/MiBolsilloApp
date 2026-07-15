package com.delacruz.mibolsilloapp.core.notification.scheduler

interface NotificationScheduler {
    /** Programa (o reprograma, si ya existía) la verificación diaria de pagos próximos. */
    fun programarVerificacionDiaria()
}

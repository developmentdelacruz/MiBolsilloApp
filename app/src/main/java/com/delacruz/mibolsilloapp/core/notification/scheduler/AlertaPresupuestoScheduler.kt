package com.delacruz.mibolsilloapp.core.notification.scheduler

interface AlertaPresupuestoScheduler {
    /** Programa (o reprograma, si ya existía) la verificación diaria de presupuestos excedidos. */
    fun programarVerificacionDiaria()
}

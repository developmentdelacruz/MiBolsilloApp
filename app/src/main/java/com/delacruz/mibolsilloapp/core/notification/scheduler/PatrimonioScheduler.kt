package com.delacruz.mibolsilloapp.core.notification.scheduler

interface PatrimonioScheduler {
    /** Programa (o reprograma, si ya existía) el snapshot diario de patrimonio neto. */
    fun programarSnapshotDiario()
}

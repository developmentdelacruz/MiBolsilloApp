package com.delacruz.mibolsilloapp.core.notification.worker

import java.time.LocalDate

/**
 * Compromisos y Suscripciones guardan el pago como "día del mes" (Int), no una fecha
 * concreta. Esta función resuelve la próxima ocurrencia real, ajustando meses cortos
 * (ej: día 31 en febrero -> último día de febrero).
 */
fun proximaFechaDePago(diaDelMes: Int, hoy: LocalDate = LocalDate.now()): LocalDate {
    val diaEsteMes = diaDelMes.coerceAtMost(hoy.lengthOfMonth())
    val fechaEsteMes = hoy.withDayOfMonth(diaEsteMes)
    if (!fechaEsteMes.isBefore(hoy)) return fechaEsteMes

    val siguienteMes = hoy.plusMonths(1)
    val diaSiguienteMes = diaDelMes.coerceAtMost(siguienteMes.lengthOfMonth())
    return siguienteMes.withDayOfMonth(diaSiguienteMes)
}

package com.delacruz.mibolsilloapp.domain.service

import java.time.LocalDate

/**
 * Contrato para sincronizar pagos con Google Calendar. Solo la interfaz por ahora:
 * la implementación real requiere credenciales OAuth de Google Calendar API y
 * queda fuera de este alcance (el doc pide "definir la interfaz").
 */
interface CalendarSyncService {

    /** @return el id del evento creado en el calendario del usuario, para poder actualizarlo/borrarlo luego. */
    suspend fun crearEventoPago(titulo: String, fecha: LocalDate, notas: String? = null): Result<String>

    suspend fun actualizarEventoPago(eventoId: String, titulo: String, fecha: LocalDate): Result<Unit>

    suspend fun eliminarEventoPago(eventoId: String): Result<Unit>
}

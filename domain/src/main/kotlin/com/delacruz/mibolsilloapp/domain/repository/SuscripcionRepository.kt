package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.model.SuscripcionCompartida
import com.delacruz.mibolsilloapp.domain.model.SuscripcionConInvitados
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface SuscripcionRepository {
    suspend fun crear(suscripcion: Suscripcion): Long
    suspend fun actualizar(suscripcion: Suscripcion)
    suspend fun eliminar(suscripcion: Suscripcion)
    suspend fun agregarInvitado(invitado: SuscripcionCompartida): Long
    suspend fun actualizarInvitado(invitado: SuscripcionCompartida)
    suspend fun eliminarInvitado(invitado: SuscripcionCompartida)
    fun observarTodas(): Flow<List<Suscripcion>>
    fun observarConInvitados(id: Long): Flow<SuscripcionConInvitados?>
    fun observarTodasConInvitados(): Flow<List<SuscripcionConInvitados>>
    fun observarTotalPendiente(suscripcionId: Long): Flow<BigDecimal>
}

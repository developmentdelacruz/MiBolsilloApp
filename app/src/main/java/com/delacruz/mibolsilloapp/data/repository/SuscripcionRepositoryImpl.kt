package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.SuscripcionDao
import com.delacruz.mibolsilloapp.data.mapper.centavosToMonto
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.model.SuscripcionCompartida
import com.delacruz.mibolsilloapp.domain.model.SuscripcionConInvitados
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SuscripcionRepositoryImpl @Inject constructor(
    private val dao: SuscripcionDao,
) : SuscripcionRepository {

    override suspend fun crear(suscripcion: Suscripcion): Long = dao.insert(suscripcion.toEntity())

    override suspend fun actualizar(suscripcion: Suscripcion) = dao.update(suscripcion.toEntity())

    override suspend fun eliminar(suscripcion: Suscripcion) = dao.delete(suscripcion.toEntity())

    override suspend fun agregarInvitado(invitado: SuscripcionCompartida): Long =
        dao.insertInvitado(invitado.toEntity())

    override suspend fun actualizarInvitado(invitado: SuscripcionCompartida) =
        dao.updateInvitado(invitado.toEntity())

    override suspend fun eliminarInvitado(invitado: SuscripcionCompartida) =
        dao.deleteInvitado(invitado.toEntity())

    override fun observarTodas(): Flow<List<Suscripcion>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarConInvitados(id: Long): Flow<SuscripcionConInvitados?> =
        dao.observeConInvitados(id).map { it?.toDomain() }

    override fun observarTodasConInvitados(): Flow<List<SuscripcionConInvitados>> =
        dao.observeTodasConInvitados().map { filas -> filas.map { it.toDomain() } }

    override fun observarTotalPendiente(suscripcionId: Long): Flow<BigDecimal> =
        dao.observeTotalPorEstado(suscripcionId, EstadoPago.PENDIENTE).map { it.centavosToMonto() }
}

package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.CompromisoDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.CompromisoConPagos
import com.delacruz.mibolsilloapp.domain.model.CompromisoConSaldo
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompromisoRepositoryImpl @Inject constructor(
    private val dao: CompromisoDao,
) : CompromisoRepository {

    override suspend fun crear(compromiso: Compromiso): Long = dao.insert(compromiso.toEntity())

    override suspend fun actualizar(compromiso: Compromiso) = dao.update(compromiso.toEntity())

    override suspend fun eliminar(compromiso: Compromiso) = dao.delete(compromiso.toEntity())

    override suspend fun registrarPago(pago: PagoCompromiso): Long = dao.insertPago(pago.toEntity())

    override suspend fun eliminarPago(pago: PagoCompromiso) = dao.deletePago(pago.toEntity())

    override fun observarTodos(): Flow<List<Compromiso>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarConPagos(id: Long): Flow<CompromisoConPagos?> =
        dao.observeConPagos(id).map { it?.toDomain() }

    override fun observarConSaldo(id: Long): Flow<CompromisoConSaldo?> =
        dao.observeConSaldo(id).map { it?.toDomain() }

    override fun observarTodosConSaldo(): Flow<List<CompromisoConSaldo>> =
        dao.observeTodosConSaldo().map { filas -> filas.map { it.toDomain() } }

    override fun observarTotalSaldoPendiente(): Flow<BigDecimal> =
        dao.observeTodosConSaldo().map { filas ->
            filas
                .filter { it.compromiso.estado == EstadoCompromiso.ACTIVO }
                .fold(BigDecimal.ZERO) { acumulado, fila -> acumulado + fila.toDomain().saldoPendiente }
        }
}

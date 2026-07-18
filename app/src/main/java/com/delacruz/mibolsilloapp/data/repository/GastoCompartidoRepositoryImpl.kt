package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.GastoCompartidoDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.GastoCompartido
import com.delacruz.mibolsilloapp.domain.model.GastoCompartidoConTransaccion
import com.delacruz.mibolsilloapp.domain.repository.GastoCompartidoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GastoCompartidoRepositoryImpl @Inject constructor(
    private val dao: GastoCompartidoDao,
) : GastoCompartidoRepository {

    override suspend fun agregar(gasto: GastoCompartido): Long = dao.insert(gasto.toEntity())

    override suspend fun actualizar(gasto: GastoCompartido) = dao.update(gasto.toEntity())

    override suspend fun eliminar(gasto: GastoCompartido) = dao.delete(gasto.toEntity())

    override fun observarPorTransaccion(transaccionId: Long): Flow<List<GastoCompartido>> =
        dao.observePorTransaccion(transaccionId).map { entidades -> entidades.map { it.toDomain() } }

    override fun observarTodosConTransaccion(): Flow<List<GastoCompartidoConTransaccion>> =
        dao.observeTodosConTransaccion().map { filas -> filas.map { it.toDomain() } }
}

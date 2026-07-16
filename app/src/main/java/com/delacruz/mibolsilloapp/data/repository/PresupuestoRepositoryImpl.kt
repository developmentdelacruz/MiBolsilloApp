package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.PresupuestoDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresupuestoRepositoryImpl @Inject constructor(
    private val dao: PresupuestoDao,
) : PresupuestoRepository {

    override suspend fun crear(presupuesto: Presupuesto): Long = dao.insert(presupuesto.toEntity())

    override suspend fun actualizar(presupuesto: Presupuesto) = dao.update(presupuesto.toEntity())

    override suspend fun eliminar(presupuesto: Presupuesto) = dao.delete(presupuesto.toEntity())

    override fun observarTodosConConsumo(): Flow<List<PresupuestoConConsumo>> =
        dao.observeTodosConConsumo().map { filas -> filas.map { it.toDomain() } }
}

package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.ProyectoDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConCosto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConTransacciones
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProyectoRepositoryImpl @Inject constructor(
    private val dao: ProyectoDao,
) : ProyectoRepository {

    override suspend fun crear(proyecto: Proyecto): Long = dao.insert(proyecto.toEntity())

    override suspend fun actualizar(proyecto: Proyecto) = dao.update(proyecto.toEntity())

    override suspend fun eliminar(proyecto: Proyecto) = dao.delete(proyecto.toEntity())

    override fun observarTodos(): Flow<List<Proyecto>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarConTransacciones(id: Long): Flow<ProyectoConTransacciones?> =
        dao.observeConTransacciones(id).map { it?.toDomain() }

    override fun observarConCosto(id: Long): Flow<ProyectoConCosto?> =
        dao.observeConCosto(id).map { it?.toDomain() }

    override fun observarTodosConCosto(): Flow<List<ProyectoConCosto>> =
        dao.observeTodosConCosto().map { filas -> filas.map { it.toDomain() } }
}

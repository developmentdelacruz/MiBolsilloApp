package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.NegocioDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NegocioRepositoryImpl @Inject constructor(
    private val dao: NegocioDao,
) : NegocioRepository {

    override suspend fun crear(negocio: Negocio): Long = dao.insert(negocio.toEntity())

    override suspend fun actualizar(negocio: Negocio) = dao.update(negocio.toEntity())

    override suspend fun eliminar(negocio: Negocio) = dao.delete(negocio.toEntity())

    override fun observarPorId(id: Long): Flow<Negocio?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observarTodos(): Flow<List<Negocio>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }
}

package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.CompraDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Compra
import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompraRepositoryImpl @Inject constructor(
    private val dao: CompraDao,
) : CompraRepository {

    override suspend fun crear(compra: Compra): Long = dao.insert(compra.toEntity())

    override suspend fun actualizar(compra: Compra) = dao.update(compra.toEntity())

    override suspend fun eliminarRegistro(compra: Compra) = dao.delete(compra.toEntity())

    override fun observarTodas(): Flow<List<Compra>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }
}

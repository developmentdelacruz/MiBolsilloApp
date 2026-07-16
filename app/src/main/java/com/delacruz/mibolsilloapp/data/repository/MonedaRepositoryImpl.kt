package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.MonedaDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonedaRepositoryImpl @Inject constructor(
    private val dao: MonedaDao,
) : MonedaRepository {

    override suspend fun crear(moneda: Moneda): Long = dao.insert(moneda.toEntity())

    override suspend fun actualizar(moneda: Moneda) = dao.update(moneda.toEntity())

    override suspend fun eliminar(moneda: Moneda) = dao.delete(moneda.toEntity())

    override fun observarTodas(): Flow<List<Moneda>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarPredeterminada(): Flow<Moneda?> =
        dao.observePredeterminada().map { it?.toDomain() }
}

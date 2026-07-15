package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.CategoriaDao
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoriaRepositoryImpl @Inject constructor(
    private val dao: CategoriaDao,
) : CategoriaRepository {

    override suspend fun crear(categoria: Categoria): Long = dao.insert(categoria.toEntity())

    override suspend fun actualizar(categoria: Categoria) = dao.update(categoria.toEntity())

    override suspend fun eliminar(categoria: Categoria) = dao.delete(categoria.toEntity())

    override fun observarPorId(id: Long): Flow<Categoria?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observarTodas(): Flow<List<Categoria>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarPorTipo(tipo: TipoCategoria): Flow<List<Categoria>> =
        dao.observeByTipo(tipo).map { entidades -> entidades.map { it.toDomain() } }
}

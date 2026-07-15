package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    suspend fun crear(categoria: Categoria): Long
    suspend fun actualizar(categoria: Categoria)
    suspend fun eliminar(categoria: Categoria)
    fun observarPorId(id: Long): Flow<Categoria?>
    fun observarTodas(): Flow<List<Categoria>>
    fun observarPorTipo(tipo: TipoCategoria): Flow<List<Categoria>>
}

package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Negocio
import kotlinx.coroutines.flow.Flow

interface NegocioRepository {
    suspend fun crear(negocio: Negocio): Long
    suspend fun actualizar(negocio: Negocio)
    suspend fun eliminar(negocio: Negocio)
    fun observarPorId(id: Long): Flow<Negocio?>
    fun observarTodos(): Flow<List<Negocio>>
}

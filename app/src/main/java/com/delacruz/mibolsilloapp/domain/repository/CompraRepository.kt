package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Compra
import kotlinx.coroutines.flow.Flow

interface CompraRepository {
    suspend fun crear(compra: Compra): Long
    suspend fun actualizar(compra: Compra)
    suspend fun eliminarRegistro(compra: Compra)
    fun observarTodas(): Flow<List<Compra>>
}

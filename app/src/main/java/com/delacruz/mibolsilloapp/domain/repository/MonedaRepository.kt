package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Moneda
import kotlinx.coroutines.flow.Flow

interface MonedaRepository {
    suspend fun crear(moneda: Moneda): Long
    suspend fun actualizar(moneda: Moneda)
    suspend fun eliminar(moneda: Moneda)
    fun observarTodas(): Flow<List<Moneda>>
    fun observarPredeterminada(): Flow<Moneda?>
}

package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import kotlinx.coroutines.flow.Flow

interface PresupuestoRepository {
    suspend fun crear(presupuesto: Presupuesto): Long
    suspend fun actualizar(presupuesto: Presupuesto)
    suspend fun eliminar(presupuesto: Presupuesto)
    fun observarTodosConConsumo(): Flow<List<PresupuestoConConsumo>>
}

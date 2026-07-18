package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.GastoCompartido
import com.delacruz.mibolsilloapp.domain.model.GastoCompartidoConTransaccion
import kotlinx.coroutines.flow.Flow

interface GastoCompartidoRepository {
    suspend fun agregar(gasto: GastoCompartido): Long
    suspend fun actualizar(gasto: GastoCompartido)
    suspend fun eliminar(gasto: GastoCompartido)
    fun observarPorTransaccion(transaccionId: Long): Flow<List<GastoCompartido>>
    fun observarTodosConTransaccion(): Flow<List<GastoCompartidoConTransaccion>>
}

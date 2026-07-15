package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.CompromisoConPagos
import com.delacruz.mibolsilloapp.domain.model.CompromisoConSaldo
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso
import kotlinx.coroutines.flow.Flow

interface CompromisoRepository {
    suspend fun crear(compromiso: Compromiso): Long
    suspend fun actualizar(compromiso: Compromiso)
    suspend fun eliminar(compromiso: Compromiso)
    suspend fun registrarPago(pago: PagoCompromiso): Long
    suspend fun eliminarPago(pago: PagoCompromiso)
    fun observarTodos(): Flow<List<Compromiso>>
    fun observarConPagos(id: Long): Flow<CompromisoConPagos?>
    fun observarConSaldo(id: Long): Flow<CompromisoConSaldo?>
    fun observarTodosConSaldo(): Flow<List<CompromisoConSaldo>>
}

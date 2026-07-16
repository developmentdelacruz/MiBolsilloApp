package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Transaccion
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface TransaccionRepository {
    suspend fun crear(transaccion: Transaccion): Long
    suspend fun actualizar(transaccion: Transaccion)
    suspend fun eliminar(transaccion: Transaccion)
    fun observarTodas(): Flow<List<Transaccion>>
    fun observarPorNegocio(negocioId: Long): Flow<List<Transaccion>>
    fun observarPorProyecto(proyectoId: Long): Flow<List<Transaccion>>
    fun observarPorRangoFechas(desde: LocalDate, hasta: LocalDate): Flow<List<Transaccion>>
    fun observarBalanceNeto(desde: LocalDate, hasta: LocalDate): Flow<BigDecimal>
}

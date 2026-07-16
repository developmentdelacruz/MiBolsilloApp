package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.CuentaConSaldo
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow

interface CuentaRepository {
    suspend fun crear(cuenta: Cuenta): Long
    suspend fun actualizar(cuenta: Cuenta)
    suspend fun eliminar(cuenta: Cuenta)
    fun observarTodas(): Flow<List<Cuenta>>
    fun observarConSaldo(cuentaId: Long): Flow<CuentaConSaldo?>
    fun observarTodasConSaldo(): Flow<List<CuentaConSaldo>>
    fun observarSaldoTotal(): Flow<BigDecimal>
}

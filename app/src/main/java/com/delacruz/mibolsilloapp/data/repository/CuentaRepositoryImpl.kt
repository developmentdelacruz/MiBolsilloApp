package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.CuentaDao
import com.delacruz.mibolsilloapp.data.mapper.centavosToMonto
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.CuentaConSaldo
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CuentaRepositoryImpl @Inject constructor(
    private val dao: CuentaDao,
) : CuentaRepository {

    override suspend fun crear(cuenta: Cuenta): Long = dao.insert(cuenta.toEntity())

    override suspend fun actualizar(cuenta: Cuenta) = dao.update(cuenta.toEntity())

    override suspend fun eliminar(cuenta: Cuenta) = dao.delete(cuenta.toEntity())

    override fun observarTodas(): Flow<List<Cuenta>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarConSaldo(cuentaId: Long): Flow<CuentaConSaldo?> =
        dao.observeConSaldo(cuentaId).map { it?.toDomain() }

    override fun observarTodasConSaldo(): Flow<List<CuentaConSaldo>> =
        dao.observeTodasConSaldo().map { filas -> filas.map { it.toDomain() } }

    override fun observarSaldoTotal(): Flow<BigDecimal> =
        dao.observeSaldoTotal().map { it.centavosToMonto() }
}

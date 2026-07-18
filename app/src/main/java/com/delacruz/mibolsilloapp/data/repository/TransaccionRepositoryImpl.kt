package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.TransaccionDao
import com.delacruz.mibolsilloapp.data.mapper.centavosToMonto
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.data.mapper.toEntity
import com.delacruz.mibolsilloapp.domain.model.GastoMensual
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransaccionRepositoryImpl @Inject constructor(
    private val dao: TransaccionDao,
) : TransaccionRepository {

    override suspend fun crear(transaccion: Transaccion): Long = dao.insert(transaccion.toEntity())

    override suspend fun actualizar(transaccion: Transaccion) = dao.update(transaccion.toEntity())

    override suspend fun eliminar(transaccion: Transaccion) = dao.delete(transaccion.toEntity())

    override fun observarTodas(): Flow<List<Transaccion>> =
        dao.observeAll().map { entidades -> entidades.map { it.toDomain() } }

    override fun observarPorNegocio(negocioId: Long): Flow<List<Transaccion>> =
        dao.observeByNegocio(negocioId).map { entidades -> entidades.map { it.toDomain() } }

    override fun observarPorProyecto(proyectoId: Long): Flow<List<Transaccion>> =
        dao.observeByProyecto(proyectoId).map { entidades -> entidades.map { it.toDomain() } }

    override fun observarPorRangoFechas(desde: LocalDate, hasta: LocalDate): Flow<List<Transaccion>> =
        dao.observeByRangoFechas(desde, hasta).map { entidades -> entidades.map { it.toDomain() } }

    override fun observarBalanceNeto(desde: LocalDate, hasta: LocalDate): Flow<BigDecimal> =
        dao.observeBalanceNeto(desde, hasta).map { it.centavosToMonto() }

    override suspend fun gastoDeCategoriaEnMes(categoriaId: Long, anio: Int, mes: Int): BigDecimal =
        dao.gastoDeCategoriaEnMes(categoriaId, anio, mes).centavosToMonto()

    override fun observarGastoMensualPorCategoria(categoriaId: Long, meses: Int): Flow<List<GastoMensual>> {
        val desde = LocalDate.now().minusMonths(meses.toLong()).withDayOfMonth(1)
        return dao.observeGastoMensualPorCategoria(categoriaId, desde).map { filas -> filas.map { it.toDomain() } }
    }

    override suspend fun eliminarCuotasFuturasDeCompra(compraId: Long, hoy: LocalDate) =
        dao.deleteCuotasFuturasDeCompra(compraId, hoy)

    override suspend fun desvincularCuotasDeCompra(compraId: Long) =
        dao.desvincularCuotasDeCompra(compraId)
}

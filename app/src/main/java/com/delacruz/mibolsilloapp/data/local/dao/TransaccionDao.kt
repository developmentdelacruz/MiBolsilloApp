package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.TransaccionEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaccion: TransaccionEntity): Long

    @Update
    suspend fun update(transaccion: TransaccionEntity)

    @Delete
    suspend fun delete(transaccion: TransaccionEntity)

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun observeAll(): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE negocioId = :negocioId ORDER BY fecha DESC")
    fun observeByNegocio(negocioId: Long): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE proyectoId = :proyectoId ORDER BY fecha DESC")
    fun observeByProyecto(proyectoId: Long): Flow<List<TransaccionEntity>>

    @Query(
        "SELECT * FROM transacciones WHERE fecha BETWEEN :desde AND :hasta ORDER BY fecha DESC",
    )
    fun observeByRangoFechas(desde: LocalDate, hasta: LocalDate): Flow<List<TransaccionEntity>>

    /** Positivo = superávit, negativo = déficit, en centavos. */
    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN montoCentavos ELSE -montoCentavos END), 0)
        FROM transacciones
        WHERE fecha BETWEEN :desde AND :hasta
        """,
    )
    fun observeBalanceNeto(desde: LocalDate, hasta: LocalDate): Flow<Long>

    /** Usado para el rollover de presupuesto: gasto de una categoría en un mes puntual (no el actual). */
    @Query(
        """
        SELECT COALESCE(SUM(montoCentavos), 0)
        FROM transacciones
        WHERE categoriaId = :categoriaId
            AND tipo = 'GASTO'
            AND strftime('%Y', fecha) = printf('%04d', :anio)
            AND strftime('%m', fecha) = printf('%02d', :mes)
        """,
    )
    suspend fun gastoDeCategoriaEnMes(categoriaId: Long, anio: Int, mes: Int): Long

    /** Cuotas futuras de una compra cancelada: se borran (todavía no se ejecutó el gasto real). */
    @Query("DELETE FROM transacciones WHERE compraId = :compraId AND fecha > :hoy")
    suspend fun deleteCuotasFuturasDeCompra(compraId: Long, hoy: LocalDate)

    /** Cuotas pasadas de una compra cancelada: quedan como transacción normal, el dinero ya se movió. */
    @Query("UPDATE transacciones SET compraId = NULL, numeroCuota = NULL WHERE compraId = :compraId")
    suspend fun desvincularCuotasDeCompra(compraId: Long)

    /** Historial mensual de gasto de una categoría, para el LineChart de Presupuestos. */
    @Query(
        """
        SELECT strftime('%Y-%m', fecha) AS mes, COALESCE(SUM(montoCentavos), 0) AS montoCentavos
        FROM transacciones
        WHERE categoriaId = :categoriaId
            AND tipo = 'GASTO'
            AND fecha >= :desde
        GROUP BY mes
        ORDER BY mes ASC
        """,
    )
    fun observeGastoMensualPorCategoria(categoriaId: Long, desde: LocalDate): Flow<List<GastoMensualRow>>
}

data class GastoMensualRow(val mes: String, val montoCentavos: Long)

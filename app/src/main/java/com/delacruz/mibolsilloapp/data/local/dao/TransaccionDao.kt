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
}

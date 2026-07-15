package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.CompromisoEntity
import com.delacruz.mibolsilloapp.data.local.entity.PagoCompromisoEntity
import com.delacruz.mibolsilloapp.data.local.relation.CompromisoConPagos
import com.delacruz.mibolsilloapp.data.local.relation.CompromisoConSaldo
import kotlinx.coroutines.flow.Flow

@Dao
interface CompromisoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(compromiso: CompromisoEntity): Long

    @Update
    suspend fun update(compromiso: CompromisoEntity)

    @Delete
    suspend fun delete(compromiso: CompromisoEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPago(pago: PagoCompromisoEntity): Long

    @Delete
    suspend fun deletePago(pago: PagoCompromisoEntity)

    @Query("SELECT * FROM compromisos ORDER BY diaPagoSugerido ASC")
    fun observeAll(): Flow<List<CompromisoEntity>>

    @Transaction
    @Query("SELECT * FROM compromisos WHERE id = :compromisoId")
    fun observeConPagos(compromisoId: Long): Flow<CompromisoConPagos?>

    /**
     * Saldo pendiente = montoTotal - suma de pagos registrados (abonos extraordinarios
     * incluidos, sin importar la fecha en que se registraron). Se recalcula en cada
     * emisión porque es un LEFT JOIN + SUM sobre datos siempre actuales, no un campo
     * cacheado que haya que sincronizar manualmente.
     */
    @Query(
        """
        SELECT c.*,
            (c.montoTotalCentavos - COALESCE(SUM(p.montoPagadoCentavos), 0)) AS saldoPendienteCentavos,
            COUNT(p.id) AS cuotasPagadas
        FROM compromisos c
        LEFT JOIN pagos_compromisos p ON p.compromisoId = c.id
        WHERE c.id = :compromisoId
        GROUP BY c.id
        """,
    )
    fun observeConSaldo(compromisoId: Long): Flow<CompromisoConSaldo?>

    @Query(
        """
        SELECT c.*,
            (c.montoTotalCentavos - COALESCE(SUM(p.montoPagadoCentavos), 0)) AS saldoPendienteCentavos,
            COUNT(p.id) AS cuotasPagadas
        FROM compromisos c
        LEFT JOIN pagos_compromisos p ON p.compromisoId = c.id
        GROUP BY c.id
        ORDER BY c.diaPagoSugerido ASC
        """,
    )
    fun observeTodosConSaldo(): Flow<List<CompromisoConSaldo>>
}

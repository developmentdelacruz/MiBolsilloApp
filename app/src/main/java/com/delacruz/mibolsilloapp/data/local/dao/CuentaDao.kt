package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.CuentaEntity
import com.delacruz.mibolsilloapp.data.local.relation.CuentaConSaldo
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cuenta: CuentaEntity): Long

    @Update
    suspend fun update(cuenta: CuentaEntity)

    @Delete
    suspend fun delete(cuenta: CuentaEntity)

    @Query("SELECT * FROM cuentas ORDER BY nombre ASC")
    fun observeAll(): Flow<List<CuentaEntity>>

    /** saldoActual = saldoInicial + suma de ingresos - suma de gastos de esa cuenta. */
    @Query(
        """
        SELECT cu.*,
            (cu.saldoInicialCentavos +
                COALESCE(SUM(CASE WHEN t.tipo = 'INGRESO' THEN t.montoCentavos ELSE -t.montoCentavos END), 0)
            ) AS saldoActualCentavos
        FROM cuentas cu
        LEFT JOIN transacciones t ON t.cuentaId = cu.id
        WHERE cu.id = :cuentaId
        GROUP BY cu.id
        """,
    )
    fun observeConSaldo(cuentaId: Long): Flow<CuentaConSaldo?>

    @Query(
        """
        SELECT cu.*,
            (cu.saldoInicialCentavos +
                COALESCE(SUM(CASE WHEN t.tipo = 'INGRESO' THEN t.montoCentavos ELSE -t.montoCentavos END), 0)
            ) AS saldoActualCentavos
        FROM cuentas cu
        LEFT JOIN transacciones t ON t.cuentaId = cu.id
        GROUP BY cu.id
        ORDER BY cu.nombre ASC
        """,
    )
    fun observeTodasConSaldo(): Flow<List<CuentaConSaldo>>

    @Query(
        """
        SELECT COALESCE(SUM(cu.saldoInicialCentavos), 0) + COALESCE(
            (SELECT SUM(CASE WHEN t.tipo = 'INGRESO' THEN t.montoCentavos ELSE -t.montoCentavos END) FROM transacciones t),
            0
        )
        FROM cuentas cu
        """,
    )
    fun observeSaldoTotal(): Flow<Long>
}

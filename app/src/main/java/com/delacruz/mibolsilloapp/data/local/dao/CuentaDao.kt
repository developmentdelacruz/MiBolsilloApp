package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.CuentaEntity
import com.delacruz.mibolsilloapp.data.local.relation.CuentaConSaldo
import java.time.LocalDate
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

    /**
     * saldoActual = saldoInicial + suma de ingresos - suma de gastos de esa cuenta.
     * Para TARJETA, cada transacción cuenta de inmediato (así funciona un límite de
     * crédito real: se bloquea completo al momento de la compra). Para el resto, una
     * transacción con fecha futura (ej. una cuota de una compra) todavía no afecta el
     * saldo — recién cuenta cuando su fecha ya pasó.
     */
    @Query(
        """
        SELECT cu.*,
            (cu.saldoInicialCentavos +
                COALESCE(SUM(
                    CASE
                        WHEN cu.tipo != 'TARJETA' AND t.fecha > :hoy THEN 0
                        WHEN t.tipo = 'INGRESO' THEN t.montoCentavos
                        ELSE -t.montoCentavos
                    END
                ), 0)
            ) AS saldoActualCentavos
        FROM cuentas cu
        LEFT JOIN transacciones t ON t.cuentaId = cu.id
        WHERE cu.id = :cuentaId
        GROUP BY cu.id
        """,
    )
    fun observeConSaldo(cuentaId: Long, hoy: LocalDate): Flow<CuentaConSaldo?>

    @Query(
        """
        SELECT cu.*,
            (cu.saldoInicialCentavos +
                COALESCE(SUM(
                    CASE
                        WHEN cu.tipo != 'TARJETA' AND t.fecha > :hoy THEN 0
                        WHEN t.tipo = 'INGRESO' THEN t.montoCentavos
                        ELSE -t.montoCentavos
                    END
                ), 0)
            ) AS saldoActualCentavos
        FROM cuentas cu
        LEFT JOIN transacciones t ON t.cuentaId = cu.id
        GROUP BY cu.id
        ORDER BY cu.nombre ASC
        """,
    )
    fun observeTodasConSaldo(hoy: LocalDate): Flow<List<CuentaConSaldo>>

    @Query(
        """
        SELECT COALESCE(SUM(cu.saldoInicialCentavos), 0) + COALESCE(
            (SELECT SUM(
                CASE
                    WHEN c2.tipo != 'TARJETA' AND t.fecha > :hoy THEN 0
                    WHEN t.tipo = 'INGRESO' THEN t.montoCentavos
                    ELSE -t.montoCentavos
                END
            )
            FROM transacciones t
            INNER JOIN cuentas c2 ON c2.id = t.cuentaId),
            0
        )
        FROM cuentas cu
        """,
    )
    fun observeSaldoTotal(hoy: LocalDate): Flow<Long>
}

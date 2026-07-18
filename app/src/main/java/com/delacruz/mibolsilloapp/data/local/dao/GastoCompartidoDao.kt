package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.GastoCompartidoEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoCompartidoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(gasto: GastoCompartidoEntity): Long

    @Update
    suspend fun update(gasto: GastoCompartidoEntity)

    @Delete
    suspend fun delete(gasto: GastoCompartidoEntity)

    @Query("SELECT * FROM gastos_compartidos WHERE transaccionId = :transaccionId")
    fun observePorTransaccion(transaccionId: Long): Flow<List<GastoCompartidoEntity>>

    @Query(
        """
        SELECT
            g.id AS gasto_id, g.transaccionId AS gasto_transaccionId, g.nombreContacto AS gasto_nombreContacto,
            g.telefono AS gasto_telefono, g.montoAPagarCentavos AS gasto_montoAPagarCentavos,
            g.estadoPago AS gasto_estadoPago,
            t.descripcion AS descripcionTransaccion, t.fecha AS fechaTransaccion
        FROM gastos_compartidos g
        INNER JOIN transacciones t ON t.id = g.transaccionId
        ORDER BY t.fecha DESC
        """,
    )
    fun observeTodosConTransaccion(): Flow<List<GastoCompartidoConTransaccionRow>>
}

data class GastoCompartidoConTransaccionRow(
    @Embedded(prefix = "gasto_") val gasto: GastoCompartidoEntity,
    val descripcionTransaccion: String,
    val fechaTransaccion: LocalDate,
)

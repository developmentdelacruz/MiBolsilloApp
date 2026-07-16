package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.PresupuestoEntity
import com.delacruz.mibolsilloapp.data.local.relation.PresupuestoConConsumo
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(presupuesto: PresupuestoEntity): Long

    @Update
    suspend fun update(presupuesto: PresupuestoEntity)

    @Delete
    suspend fun delete(presupuesto: PresupuestoEntity)

    /**
     * Consumo del mes en curso. 'fecha' se guarda en ISO-8601 como TEXT
     * (ver Converters.kt), por eso strftime funciona directo sin conversión.
     */
    @Query(
        """
        SELECT
            p.id AS presupuesto_id,
            p.categoriaId AS presupuesto_categoriaId,
            p.montoMensualCentavos AS presupuesto_montoMensualCentavos,
            c.id AS categoria_id,
            c.nombre AS categoria_nombre,
            c.icono AS categoria_icono,
            c.tipo AS categoria_tipo,
            COALESCE(SUM(t.montoCentavos), 0) AS consumidoCentavos
        FROM presupuestos p
        INNER JOIN categorias c ON c.id = p.categoriaId
        LEFT JOIN transacciones t
            ON t.categoriaId = p.categoriaId
            AND t.tipo = 'GASTO'
            AND strftime('%Y-%m', t.fecha) = strftime('%Y-%m', 'now')
        GROUP BY p.id
        ORDER BY c.nombre ASC
        """,
    )
    fun observeTodosConConsumo(): Flow<List<PresupuestoConConsumo>>
}

package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.ProyectoEntity
import com.delacruz.mibolsilloapp.data.local.relation.ProyectoConCosto
import com.delacruz.mibolsilloapp.data.local.relation.ProyectoConTransacciones
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(proyecto: ProyectoEntity): Long

    @Update
    suspend fun update(proyecto: ProyectoEntity)

    @Delete
    suspend fun delete(proyecto: ProyectoEntity)

    @Query("SELECT * FROM proyectos ORDER BY nombre ASC")
    fun observeAll(): Flow<List<ProyectoEntity>>

    @Transaction
    @Query("SELECT * FROM proyectos WHERE id = :proyectoId")
    fun observeConTransacciones(proyectoId: Long): Flow<ProyectoConTransacciones?>

    /**
     * Costo acumulado = suma de transacciones tipo GASTO ligadas al proyecto
     * (mano de obra, materiales, etc). Los INGRESO no restan presupuesto:
     * si el proyecto recibe un reembolso, no "libera" presupuesto gastado.
     */
    @Query(
        """
        SELECT p.*,
            COALESCE(SUM(CASE WHEN t.tipo = 'GASTO' THEN t.montoCentavos ELSE 0 END), 0) AS costoAcumuladoCentavos,
            (p.presupuestoEstimadoCentavos - COALESCE(SUM(CASE WHEN t.tipo = 'GASTO' THEN t.montoCentavos ELSE 0 END), 0)) AS presupuestoRestanteCentavos
        FROM proyectos p
        LEFT JOIN transacciones t ON t.proyectoId = p.id
        WHERE p.id = :proyectoId
        GROUP BY p.id
        """,
    )
    fun observeConCosto(proyectoId: Long): Flow<ProyectoConCosto?>

    @Query(
        """
        SELECT p.*,
            COALESCE(SUM(CASE WHEN t.tipo = 'GASTO' THEN t.montoCentavos ELSE 0 END), 0) AS costoAcumuladoCentavos,
            (p.presupuestoEstimadoCentavos - COALESCE(SUM(CASE WHEN t.tipo = 'GASTO' THEN t.montoCentavos ELSE 0 END), 0)) AS presupuestoRestanteCentavos
        FROM proyectos p
        LEFT JOIN transacciones t ON t.proyectoId = p.id
        GROUP BY p.id
        ORDER BY p.nombre ASC
        """,
    )
    fun observeTodosConCosto(): Flow<List<ProyectoConCosto>>
}

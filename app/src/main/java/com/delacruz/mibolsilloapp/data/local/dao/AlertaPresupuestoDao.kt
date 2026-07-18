package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.delacruz.mibolsilloapp.data.local.entity.AlertaPresupuestoEntity

@Dao
interface AlertaPresupuestoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alerta: AlertaPresupuestoEntity)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM alertas_presupuesto " +
            "WHERE presupuestoId = :presupuestoId AND mes = :mes AND nivel = :nivel)",
    )
    suspend fun existe(presupuestoId: Long, mes: String, nivel: String): Boolean
}

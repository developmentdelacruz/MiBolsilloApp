package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionCompartidaEntity
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionEntity
import com.delacruz.mibolsilloapp.data.local.relation.SuscripcionConInvitados
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import kotlinx.coroutines.flow.Flow

@Dao
interface SuscripcionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(suscripcion: SuscripcionEntity): Long

    @Update
    suspend fun update(suscripcion: SuscripcionEntity)

    @Delete
    suspend fun delete(suscripcion: SuscripcionEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInvitado(invitado: SuscripcionCompartidaEntity): Long

    @Update
    suspend fun updateInvitado(invitado: SuscripcionCompartidaEntity)

    @Delete
    suspend fun deleteInvitado(invitado: SuscripcionCompartidaEntity)

    @Query("SELECT * FROM suscripciones ORDER BY diaCobro ASC")
    fun observeAll(): Flow<List<SuscripcionEntity>>

    @Transaction
    @Query("SELECT * FROM suscripciones WHERE id = :suscripcionId")
    fun observeConInvitados(suscripcionId: Long): Flow<SuscripcionConInvitados?>

    @Transaction
    @Query("SELECT * FROM suscripciones ORDER BY diaCobro ASC")
    fun observeTodasConInvitados(): Flow<List<SuscripcionConInvitados>>

    @Query(
        """
        SELECT COALESCE(SUM(montoAPagarCentavos), 0)
        FROM suscripciones_compartidas
        WHERE suscripcionId = :suscripcionId AND estadoPago = :estado
        """,
    )
    fun observeTotalPorEstado(
        suscripcionId: Long,
        estado: EstadoPago = EstadoPago.PENDIENTE,
    ): Flow<Long>
}

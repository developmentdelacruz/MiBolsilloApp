package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.NegocioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NegocioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(negocio: NegocioEntity): Long

    @Update
    suspend fun update(negocio: NegocioEntity)

    @Delete
    suspend fun delete(negocio: NegocioEntity)

    @Query("SELECT * FROM negocios WHERE id = :id")
    fun observeById(id: Long): Flow<NegocioEntity?>

    @Query("SELECT * FROM negocios ORDER BY nombre ASC")
    fun observeAll(): Flow<List<NegocioEntity>>
}

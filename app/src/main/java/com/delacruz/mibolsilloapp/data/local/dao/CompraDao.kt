package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.CompraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(compra: CompraEntity): Long

    @Update
    suspend fun update(compra: CompraEntity)

    @Delete
    suspend fun delete(compra: CompraEntity)

    @Query("SELECT * FROM compras ORDER BY fechaPrimeraCuota DESC")
    fun observeAll(): Flow<List<CompraEntity>>
}

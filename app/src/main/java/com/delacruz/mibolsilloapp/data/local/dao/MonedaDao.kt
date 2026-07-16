package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.delacruz.mibolsilloapp.data.local.entity.MonedaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonedaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(moneda: MonedaEntity): Long

    @Update
    suspend fun update(moneda: MonedaEntity)

    @Delete
    suspend fun delete(moneda: MonedaEntity)

    @Query("SELECT * FROM monedas ORDER BY codigo ASC")
    fun observeAll(): Flow<List<MonedaEntity>>

    @Query("SELECT * FROM monedas WHERE esPredeterminada = 1 LIMIT 1")
    fun observePredeterminada(): Flow<MonedaEntity?>
}

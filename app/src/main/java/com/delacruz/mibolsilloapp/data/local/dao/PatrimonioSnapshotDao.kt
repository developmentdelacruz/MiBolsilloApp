package com.delacruz.mibolsilloapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.delacruz.mibolsilloapp.data.local.entity.PatrimonioSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatrimonioSnapshotDao {

    /** REPLACE por el índice único de 'fecha': reejecutar el worker el mismo día actualiza, no duplica. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: PatrimonioSnapshotEntity)

    @Query("SELECT * FROM patrimonio_snapshots ORDER BY fecha DESC LIMIT :limite")
    fun observeUltimos(limite: Int): Flow<List<PatrimonioSnapshotEntity>>
}

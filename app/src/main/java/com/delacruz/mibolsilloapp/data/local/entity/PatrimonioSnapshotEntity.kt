package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "patrimonio_snapshots",
    indices = [Index("fecha", unique = true)],
)
data class PatrimonioSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: LocalDate,
    val valorCentavos: Long,
)

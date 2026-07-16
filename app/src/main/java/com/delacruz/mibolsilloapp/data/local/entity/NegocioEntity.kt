package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "negocios")
data class NegocioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
)

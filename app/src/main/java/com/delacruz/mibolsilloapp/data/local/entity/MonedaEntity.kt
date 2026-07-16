package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monedas")
data class MonedaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val simbolo: String,
    val esPredeterminada: Boolean = false,
)

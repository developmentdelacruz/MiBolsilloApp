package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proyectos")
data class ProyectoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val presupuestoEstimadoCentavos: Long,
)

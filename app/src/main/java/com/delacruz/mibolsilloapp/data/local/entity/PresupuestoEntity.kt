package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "presupuestos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("categoriaId", unique = true)],
)
data class PresupuestoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoriaId: Long,
    val montoMensualCentavos: Long,
)

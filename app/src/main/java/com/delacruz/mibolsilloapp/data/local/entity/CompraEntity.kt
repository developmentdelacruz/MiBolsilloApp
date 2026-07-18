package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "compras",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = NegocioEntity::class,
            parentColumns = ["id"],
            childColumns = ["negocioId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("categoriaId"), Index("cuentaId"), Index("negocioId")],
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descripcion: String,
    val montoTotalCentavos: Long,
    val cuotasTotales: Int,
    val categoriaId: Long,
    val cuentaId: Long,
    val negocioId: Long? = null,
    val fechaPrimeraCuota: LocalDate,
)

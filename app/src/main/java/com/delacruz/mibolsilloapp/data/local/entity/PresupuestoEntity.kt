package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

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
    /** Desde cuándo existe este presupuesto — define a partir de qué mes se acumula el rollover. */
    val creadoEn: LocalDate = LocalDate.now(),
    /** Presupuesto desactivado: se excluye de "Disponible para gastar" pero se conserva su historial. */
    val activo: Boolean = true,
)

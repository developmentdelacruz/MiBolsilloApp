package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registra que ya se avisó (80% o 100%) un presupuesto en un mes dado, para que el worker de
 * alertas no repita la misma notificación cada vez que corre mientras el presupuesto siga
 * por encima del umbral.
 */
@Entity(
    tableName = "alertas_presupuesto",
    foreignKeys = [
        ForeignKey(
            entity = PresupuestoEntity::class,
            parentColumns = ["id"],
            childColumns = ["presupuestoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("presupuestoId", "mes", "nivel", unique = true)],
)
data class AlertaPresupuestoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val presupuestoId: Long,
    val mes: String,
    val nivel: String,
)

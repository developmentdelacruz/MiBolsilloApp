package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.EstadoPago

@Entity(
    tableName = "suscripciones_compartidas",
    foreignKeys = [
        ForeignKey(
            entity = SuscripcionEntity::class,
            parentColumns = ["id"],
            childColumns = ["suscripcionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("suscripcionId")],
)
data class SuscripcionCompartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val suscripcionId: Long,
    val nombreContacto: String,
    val telefono: String,
    val montoAPagarCentavos: Long,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
)

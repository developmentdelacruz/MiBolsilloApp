package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.EstadoPago

@Entity(
    tableName = "gastos_compartidos",
    foreignKeys = [
        ForeignKey(
            entity = TransaccionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaccionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("transaccionId")],
)
data class GastoCompartidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transaccionId: Long,
    val nombreContacto: String,
    val telefono: String,
    val montoAPagarCentavos: Long,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
)

package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso

@Entity(tableName = "compromisos")
data class CompromisoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    /** Monto en centavos (unidad mínima) para que SUM() en SQLite sea exacto, sin punto flotante. */
    val montoTotalCentavos: Long,
    val cuotasTotales: Int,
    val diaPagoSugerido: Int,
    val estado: EstadoCompromiso,
)

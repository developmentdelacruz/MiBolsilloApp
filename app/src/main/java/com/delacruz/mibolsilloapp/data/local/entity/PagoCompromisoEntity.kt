package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "pagos_compromisos",
    foreignKeys = [
        ForeignKey(
            entity = CompromisoEntity::class,
            parentColumns = ["id"],
            childColumns = ["compromisoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("compromisoId")],
)
data class PagoCompromisoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val compromisoId: Long,
    val fechaPagoReal: LocalDate,
    val montoPagadoCentavos: Long,
    val numeroCuota: Int,
    val esAdelantado: Boolean = false,
)

package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.delacruz.mibolsilloapp.data.local.entity.CompromisoEntity
import com.delacruz.mibolsilloapp.data.local.entity.PagoCompromisoEntity

data class CompromisoConPagos(
    @Embedded val compromiso: CompromisoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "compromisoId",
    )
    val pagos: List<PagoCompromisoEntity>,
)

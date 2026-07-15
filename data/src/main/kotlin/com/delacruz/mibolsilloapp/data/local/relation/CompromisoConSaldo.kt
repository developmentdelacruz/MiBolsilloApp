package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import com.delacruz.mibolsilloapp.data.local.entity.CompromisoEntity

/** Resultado de una consulta agregada (JOIN + SUM), no de un @Relation. */
data class CompromisoConSaldo(
    @Embedded val compromiso: CompromisoEntity,
    val saldoPendienteCentavos: Long,
    val cuotasPagadas: Int,
)

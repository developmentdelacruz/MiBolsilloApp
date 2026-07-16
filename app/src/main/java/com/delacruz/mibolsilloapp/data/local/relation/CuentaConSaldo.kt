package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import com.delacruz.mibolsilloapp.data.local.entity.CuentaEntity

/** Resultado de una consulta agregada (JOIN + SUM), no de un @Relation. */
data class CuentaConSaldo(
    @Embedded val cuenta: CuentaEntity,
    val saldoActualCentavos: Long,
)

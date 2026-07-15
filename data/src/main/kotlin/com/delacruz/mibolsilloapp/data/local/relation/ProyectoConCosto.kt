package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import com.delacruz.mibolsilloapp.data.local.entity.ProyectoEntity

/** Resultado de una consulta agregada (JOIN + SUM), no de un @Relation. */
data class ProyectoConCosto(
    @Embedded val proyecto: ProyectoEntity,
    val costoAcumuladoCentavos: Long,
    val presupuestoRestanteCentavos: Long,
)

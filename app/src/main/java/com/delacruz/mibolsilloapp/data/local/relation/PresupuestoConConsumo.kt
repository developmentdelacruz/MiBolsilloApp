package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import com.delacruz.mibolsilloapp.data.local.entity.CategoriaEntity
import com.delacruz.mibolsilloapp.data.local.entity.PresupuestoEntity

/** Resultado de un JOIN presupuesto+categoria con el consumo del mes agregado. */
data class PresupuestoConConsumo(
    @Embedded(prefix = "presupuesto_") val presupuesto: PresupuestoEntity,
    @Embedded(prefix = "categoria_") val categoria: CategoriaEntity,
    val consumidoCentavos: Long,
)

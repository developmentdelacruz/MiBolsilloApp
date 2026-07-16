package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.PresupuestoEntity
import com.delacruz.mibolsilloapp.data.local.relation.PresupuestoConConsumo as PresupuestoConConsumoRow
import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo

fun PresupuestoEntity.toDomain(): Presupuesto = Presupuesto(
    id = id,
    categoriaId = categoriaId,
    montoMensual = montoMensualCentavos.centavosToMonto(),
)

fun Presupuesto.toEntity(): PresupuestoEntity = PresupuestoEntity(
    id = id,
    categoriaId = categoriaId,
    montoMensualCentavos = montoMensual.toCentavos(),
)

fun PresupuestoConConsumoRow.toDomain(): PresupuestoConConsumo = PresupuestoConConsumo(
    presupuesto = presupuesto.toDomain(),
    categoria = categoria.toDomain(),
    consumido = consumidoCentavos.centavosToMonto(),
)

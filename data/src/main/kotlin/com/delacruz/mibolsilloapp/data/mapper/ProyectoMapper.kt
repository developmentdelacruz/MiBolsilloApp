package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.ProyectoEntity
import com.delacruz.mibolsilloapp.data.local.relation.ProyectoConCosto as ProyectoConCostoRow
import com.delacruz.mibolsilloapp.data.local.relation.ProyectoConTransacciones as ProyectoConTransaccionesRow
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConCosto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConTransacciones

fun ProyectoEntity.toDomain(): Proyecto = Proyecto(
    id = id,
    nombre = nombre,
    presupuestoEstimado = presupuestoEstimadoCentavos.centavosToMonto(),
)

fun Proyecto.toEntity(): ProyectoEntity = ProyectoEntity(
    id = id,
    nombre = nombre,
    presupuestoEstimadoCentavos = presupuestoEstimado.toCentavos(),
)

fun ProyectoConCostoRow.toDomain(): ProyectoConCosto = ProyectoConCosto(
    proyecto = proyecto.toDomain(),
    costoAcumulado = costoAcumuladoCentavos.centavosToMonto(),
    presupuestoRestante = presupuestoRestanteCentavos.centavosToMonto(),
)

fun ProyectoConTransaccionesRow.toDomain(): ProyectoConTransacciones = ProyectoConTransacciones(
    proyecto = proyecto.toDomain(),
    transacciones = transacciones.map { it.toDomain() },
)

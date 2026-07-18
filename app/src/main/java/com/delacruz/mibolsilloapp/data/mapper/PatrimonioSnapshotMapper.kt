package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.PatrimonioSnapshotEntity
import com.delacruz.mibolsilloapp.domain.model.PatrimonioSnapshot

fun PatrimonioSnapshotEntity.toDomain(): PatrimonioSnapshot = PatrimonioSnapshot(
    id = id,
    fecha = fecha,
    valor = valorCentavos.centavosToMonto(),
)

package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.CompraEntity
import com.delacruz.mibolsilloapp.domain.model.Compra

fun CompraEntity.toDomain(): Compra = Compra(
    id = id,
    descripcion = descripcion,
    montoTotal = montoTotalCentavos.centavosToMonto(),
    cuotasTotales = cuotasTotales,
    categoriaId = categoriaId,
    cuentaId = cuentaId,
    negocioId = negocioId,
    fechaPrimeraCuota = fechaPrimeraCuota,
)

fun Compra.toEntity(): CompraEntity = CompraEntity(
    id = id,
    descripcion = descripcion,
    montoTotalCentavos = montoTotal.toCentavos(),
    cuotasTotales = cuotasTotales,
    categoriaId = categoriaId,
    cuentaId = cuentaId,
    negocioId = negocioId,
    fechaPrimeraCuota = fechaPrimeraCuota,
)

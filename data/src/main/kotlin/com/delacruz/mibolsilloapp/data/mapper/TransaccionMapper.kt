package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.TransaccionEntity
import com.delacruz.mibolsilloapp.domain.model.Transaccion

fun TransaccionEntity.toDomain(): Transaccion = Transaccion(
    id = id,
    descripcion = descripcion,
    monto = montoCentavos.centavosToMonto(),
    fecha = fecha,
    tipo = tipo,
    categoriaId = categoriaId,
    negocioId = negocioId,
    proyectoId = proyectoId,
)

fun Transaccion.toEntity(): TransaccionEntity = TransaccionEntity(
    id = id,
    descripcion = descripcion,
    montoCentavos = monto.toCentavos(),
    fecha = fecha,
    tipo = tipo,
    categoriaId = categoriaId,
    negocioId = negocioId,
    proyectoId = proyectoId,
)

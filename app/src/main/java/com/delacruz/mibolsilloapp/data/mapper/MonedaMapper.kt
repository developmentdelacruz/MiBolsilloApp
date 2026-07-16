package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.MonedaEntity
import com.delacruz.mibolsilloapp.domain.model.Moneda

fun MonedaEntity.toDomain(): Moneda = Moneda(
    id = id,
    codigo = codigo,
    nombre = nombre,
    simbolo = simbolo,
    esPredeterminada = esPredeterminada,
)

fun Moneda.toEntity(): MonedaEntity = MonedaEntity(
    id = id,
    codigo = codigo,
    nombre = nombre,
    simbolo = simbolo,
    esPredeterminada = esPredeterminada,
)

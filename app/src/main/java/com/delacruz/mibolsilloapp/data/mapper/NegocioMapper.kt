package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.NegocioEntity
import com.delacruz.mibolsilloapp.domain.model.Negocio

fun NegocioEntity.toDomain(): Negocio = Negocio(id = id, nombre = nombre)

fun Negocio.toEntity(): NegocioEntity = NegocioEntity(id = id, nombre = nombre)

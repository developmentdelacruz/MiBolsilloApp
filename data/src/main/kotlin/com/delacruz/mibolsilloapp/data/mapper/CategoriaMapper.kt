package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.CategoriaEntity
import com.delacruz.mibolsilloapp.domain.model.Categoria

fun CategoriaEntity.toDomain(): Categoria = Categoria(
    id = id,
    nombre = nombre,
    icono = icono,
    tipo = tipo,
)

fun Categoria.toEntity(): CategoriaEntity = CategoriaEntity(
    id = id,
    nombre = nombre,
    icono = icono,
    tipo = tipo,
)

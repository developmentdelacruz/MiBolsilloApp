package com.delacruz.mibolsilloapp.domain.model

data class Categoria(
    val id: Long = 0,
    val nombre: String,
    val icono: String,
    val tipo: TipoCategoria,
)

enum class TipoCategoria {
    INGRESO,
    GASTO,
    NEGOCIO,
    PROYECTO,
    TARJETA,
}

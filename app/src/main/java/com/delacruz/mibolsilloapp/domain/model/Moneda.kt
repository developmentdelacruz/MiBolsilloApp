package com.delacruz.mibolsilloapp.domain.model

data class Moneda(
    val id: Long = 0,
    val codigo: String,
    val nombre: String,
    val simbolo: String,
    val esPredeterminada: Boolean = false,
)

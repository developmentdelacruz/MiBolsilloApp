package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class Proyecto(
    val id: Long = 0,
    val nombre: String,
    val presupuestoEstimado: BigDecimal,
)

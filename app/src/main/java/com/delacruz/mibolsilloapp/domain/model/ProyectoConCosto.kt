package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal

data class ProyectoConCosto(
    val proyecto: Proyecto,
    val costoAcumulado: BigDecimal,
    val presupuestoRestante: BigDecimal,
)

data class ProyectoConTransacciones(
    val proyecto: Proyecto,
    val transacciones: List<Transaccion>,
)

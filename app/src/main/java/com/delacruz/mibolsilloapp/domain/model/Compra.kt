package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Compra(
    val id: Long = 0,
    val descripcion: String,
    val montoTotal: BigDecimal,
    val cuotasTotales: Int,
    val categoriaId: Long,
    val cuentaId: Long,
    val negocioId: Long? = null,
    val fechaPrimeraCuota: LocalDate,
)

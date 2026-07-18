package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class SugerenciaRecurrencia(
    val negocioId: Long,
    val categoriaId: Long,
    val montoPromedio: BigDecimal,
    val ocurrencias: Int,
    val ultimaFecha: LocalDate,
)

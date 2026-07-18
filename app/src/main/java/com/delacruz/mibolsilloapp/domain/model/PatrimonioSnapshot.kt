package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class PatrimonioSnapshot(
    val id: Long = 0,
    val fecha: LocalDate,
    val valor: BigDecimal,
)

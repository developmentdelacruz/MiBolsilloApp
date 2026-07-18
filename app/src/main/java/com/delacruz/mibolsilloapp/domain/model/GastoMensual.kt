package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.YearMonth

/** Gasto total de una categoría en un mes puntual — un punto del historial de LineChart. */
data class GastoMensual(
    val mes: YearMonth,
    val monto: BigDecimal,
)

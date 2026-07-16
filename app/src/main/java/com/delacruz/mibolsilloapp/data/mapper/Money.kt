package com.delacruz.mibolsilloapp.data.mapper

import java.math.BigDecimal
import java.math.RoundingMode

/** Convención fija: 2 decimales (centavos) como unidad mínima de almacenamiento. */
private const val ESCALA_CENTAVOS = 2

fun BigDecimal.toCentavos(): Long =
    this.setScale(ESCALA_CENTAVOS, RoundingMode.HALF_UP).movePointRight(ESCALA_CENTAVOS).longValueExact()

fun Long.centavosToMonto(): BigDecimal =
    BigDecimal(this).movePointLeft(ESCALA_CENTAVOS)

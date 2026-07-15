package com.delacruz.mibolsilloapp.core.ui.components

import java.math.BigDecimal
import java.text.DecimalFormat

private val formatoMonto = DecimalFormat("#,##0.00")

/** Formato simple "Q 1,234.56". El símbolo llega de la Moneda seleccionada, no está hardcodeado. */
fun BigDecimal.formatearMonto(simbolo: String): String = "$simbolo ${formatoMonto.format(this)}"

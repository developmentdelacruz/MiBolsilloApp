package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import java.math.BigDecimal
import java.text.DecimalFormat

private val formatoMonto = DecimalFormat("#,##0.00")

/** Formato simple "Q 1,234.56". El símbolo llega de la Moneda seleccionada, no está hardcodeado. */
fun BigDecimal.formatearMonto(simbolo: String): String = "$simbolo ${formatoMonto.format(this)}"

/**
 * Monto con color semántico (verde ingreso / rojo gasto). Cuando [esPositivo] es null
 * se usa el color de texto por defecto (montos neutros, como un presupuesto o saldo total).
 */
@Composable
fun MontoTexto(
    texto: String,
    esPositivo: Boolean?,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val color = when (esPositivo) {
        true -> MiBolsilloTheme.extendedColors.positive
        false -> MiBolsilloTheme.extendedColors.negative
        null -> LocalContentColor.current
    }
    Text(text = texto, color = color, style = style, modifier = modifier)
}

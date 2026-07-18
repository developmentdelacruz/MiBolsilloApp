package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 *
 * Cada cambio de [texto] transiciona con slide+fade en vez de saltar — se anima el texto
 * completo (no dígito a dígito) para no requerir parsear de vuelta strings ya formateadas
 * como "Restante: Q 100.00" en los call sites existentes.
 */
@Composable
fun MontoTexto(
    texto: String,
    esPositivo: Boolean?,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val colorObjetivo = when (esPositivo) {
        true -> MiBolsilloTheme.extendedColors.positive
        false -> MiBolsilloTheme.extendedColors.negative
        null -> LocalContentColor.current
    }
    val color by animateColorAsState(targetValue = colorObjetivo, label = "colorMonto")

    AnimatedContent(
        targetState = texto,
        modifier = modifier,
        transitionSpec = {
            (slideInVertically { alto -> alto } + fadeIn())
                .togetherWith(slideOutVertically { alto -> -alto } + fadeOut())
        },
        label = "monto",
    ) { valor ->
        Text(text = valor, color = color, style = style)
    }
}

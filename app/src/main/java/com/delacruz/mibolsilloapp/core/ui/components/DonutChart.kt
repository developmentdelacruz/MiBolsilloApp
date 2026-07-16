package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val PaletaDonut = listOf(
    Color(0xFF006A5F), Color(0xFFF6BD3E), Color(0xFFB3261E),
    Color(0xFF4A6360), Color(0xFF7BF8E0), Color(0xFFB1CCC7),
)

/** Anillo de proporciones (gasto por categoría, etc.) dibujado con Canvas puro, sin librería de charts. */
@Composable
fun DonutChart(
    valores: List<Float>,
    modifier: Modifier = Modifier,
    grosor: Dp = 24.dp,
) {
    val total = valores.sum()
    Canvas(modifier = modifier.size(160.dp)) {
        if (total <= 0f) return@Canvas
        var anguloInicial = -90f
        val grosorPx = grosor.toPx()
        valores.forEachIndexed { index, valor ->
            val barrido = (valor / total) * 360f
            drawArc(
                color = PaletaDonut[index % PaletaDonut.size],
                startAngle = anguloInicial,
                sweepAngle = barrido,
                useCenter = false,
                style = Stroke(width = grosorPx, cap = StrokeCap.Butt),
                size = Size(size.width - grosorPx, size.height - grosorPx),
                topLeft = Offset(grosorPx / 2, grosorPx / 2),
            )
            anguloInicial += barrido
        }
    }
}

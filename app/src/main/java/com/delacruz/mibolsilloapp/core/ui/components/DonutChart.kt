package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

@Composable
private fun paletaDonut(): List<Color> = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.tertiary,
    MaterialTheme.colorScheme.error,
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.primaryContainer,
    MaterialTheme.colorScheme.tertiaryContainer,
)

/**
 * Anillo de proporciones (gasto por categoría, etc.), Canvas puro sin librería de charts.
 * Colores del tema (antes hardcodeados — no se adaptaban a modo oscuro). Anima el barrido
 * inicial como una "revelada" secuencial, y permite tocar una porción para resaltarla
 * (se engrosa) y ver su etiqueta/porcentaje en el centro del anillo.
 */
@Composable
fun DonutChart(
    valores: List<Float>,
    modifier: Modifier = Modifier,
    etiquetas: List<String> = emptyList(),
    grosor: Dp = 24.dp,
) {
    val total = valores.sum()
    val paleta = paletaDonut()
    var indiceSeleccionado by remember { mutableStateOf<Int?>(null) }

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(valores) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, tween(700))
    }

    Box(modifier = modifier.size(160.dp), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .pointerInput(valores) {
                    detectTapGestures { posicion ->
                        if (total <= 0f) return@detectTapGestures
                        val grosorPx = grosor.toPx()
                        val centro = Offset(size.width / 2f, size.height / 2f)
                        val dx = posicion.x - centro.x
                        val dy = posicion.y - centro.y
                        val radio = hypot(dx, dy)
                        if (radio < size.width / 2f - grosorPx * 1.5f || radio > size.width / 2f) {
                            indiceSeleccionado = null
                            return@detectTapGestures
                        }
                        var angulo = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat() + 90f
                        if (angulo < 0f) angulo += 360f
                        var acumulado = 0f
                        for (i in valores.indices) {
                            val barrido = (valores[i] / total) * 360f
                            if (angulo in acumulado..(acumulado + barrido)) {
                                indiceSeleccionado = if (indiceSeleccionado == i) null else i
                                break
                            }
                            acumulado += barrido
                        }
                    }
                },
        ) {
            if (total <= 0f) return@Canvas
            val grosorPx = grosor.toPx()
            val anguloRevelado = -90f + 360f * progreso.value
            var inicioSlice = -90f

            valores.forEachIndexed { index, valor ->
                val barridoCompleto = (valor / total) * 360f
                val finSlice = inicioSlice + barridoCompleto
                val barridoVisible = (min(finSlice, anguloRevelado) - inicioSlice).coerceAtLeast(0f)

                if (barridoVisible > 0f) {
                    val resaltado = indiceSeleccionado == index
                    val grosorEfectivo = if (resaltado) grosorPx * 1.25f else grosorPx
                    drawArc(
                        color = paleta[index % paleta.size],
                        startAngle = inicioSlice,
                        sweepAngle = barridoVisible,
                        useCenter = false,
                        style = Stroke(width = grosorEfectivo, cap = StrokeCap.Butt),
                        size = Size(size.width - grosorEfectivo, size.height - grosorEfectivo),
                        topLeft = Offset(grosorEfectivo / 2, grosorEfectivo / 2),
                    )
                }
                inicioSlice = finSlice
            }
        }

        val seleccion = indiceSeleccionado
        if (seleccion != null && seleccion < valores.size && total > 0f) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (seleccion < etiquetas.size) {
                    Text(etiquetas[seleccion], style = MaterialTheme.typography.labelSmall)
                }
                Text(
                    "${((valores[seleccion] / total) * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

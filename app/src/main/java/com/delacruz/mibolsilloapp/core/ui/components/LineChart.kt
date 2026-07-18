package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Línea de tendencia (patrimonio neto, etc.) dibujada con Canvas puro, sin librería de
 * charts. Relleno degradado bajo la línea, animación de dibujo inicial (revelada de
 * izquierda a derecha), y toque para ver el valor exacto de un punto.
 */
@Composable
fun LineChart(
    valores: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    grosor: Dp = 3.dp,
    formatoValor: (Float) -> String = { it.toString() },
    lineaReferencia: Float? = null,
    colorReferencia: Color = MaterialTheme.colorScheme.error,
) {
    var indiceSeleccionado by remember { mutableStateOf<Int?>(null) }

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(valores) {
        progreso.snapTo(0f)
        progreso.animateTo(1f, tween(700))
    }

    Box(modifier = modifier.fillMaxWidth().height(80.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .pointerInput(valores) {
                    if (valores.size < 2) return@pointerInput
                    val pasoX = size.width.toFloat() / (valores.size - 1)
                    detectTapGestures(
                        onTap = { posicion ->
                            val indice = (posicion.x / pasoX).roundToInt().coerceIn(0, valores.size - 1)
                            indiceSeleccionado = if (indiceSeleccionado == indice) null else indice
                        },
                    )
                },
        ) {
            if (valores.size < 2) return@Canvas

            val valoresConReferencia = if (lineaReferencia != null) valores + lineaReferencia else valores
            val minimo = valoresConReferencia.min()
            val maximo = valoresConReferencia.max()
            val rango = (maximo - minimo).takeIf { it > 0f } ?: 1f
            val pasoX = size.width / (valores.size - 1)
            val grosorPx = grosor.toPx()

            if (lineaReferencia != null) {
                val y = size.height - ((lineaReferencia - minimo) / rango) * size.height
                drawLine(
                    color = colorReferencia,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = grosorPx * 0.6f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)),
                )
            }

            val puntos = valores.mapIndexed { indice, valor ->
                val x = indice * pasoX
                val y = size.height - ((valor - minimo) / rango) * size.height
                Offset(x, y)
            }

            clipRect(right = size.width * progreso.value) {
                val trazo = Path().apply {
                    moveTo(puntos[0].x, puntos[0].y)
                    for (i in 1 until puntos.size) lineTo(puntos[i].x, puntos[i].y)
                }
                val relleno = Path().apply {
                    addPath(trazo)
                    lineTo(puntos.last().x, size.height)
                    lineTo(puntos.first().x, size.height)
                    close()
                }
                drawPath(
                    path = relleno,
                    brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.3f), Color.Transparent)),
                )
                drawPath(path = trazo, color = color, style = Stroke(width = grosorPx, cap = StrokeCap.Round))
            }

            indiceSeleccionado?.let { indice ->
                drawCircle(color = color, radius = grosorPx * 1.6f, center = puntos[indice])
                drawCircle(color = Color.White, radius = grosorPx * 0.7f, center = puntos[indice])
            }
        }

        val seleccion = indiceSeleccionado
        if (seleccion != null && seleccion < valores.size) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                Text(
                    text = formatoValor(valores[seleccion]),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

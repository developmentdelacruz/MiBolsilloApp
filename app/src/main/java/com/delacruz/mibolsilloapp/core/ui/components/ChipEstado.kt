package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Tono semántico de un [ChipEstado]: POSITIVO (activo/pagado), NEGATIVO (vencido/cancelado), NEUTRO. */
enum class TonoChip { POSITIVO, NEGATIVO, NEUTRO }

@Composable
fun ChipEstado(
    texto: String,
    tono: TonoChip,
    modifier: Modifier = Modifier,
) {
    val (contenedor, contenido) = when (tono) {
        TonoChip.POSITIVO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        TonoChip.NEGATIVO -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        TonoChip.NEUTRO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = contenedor,
        contentColor = contenido,
        shape = RoundedCornerShape(50),
        modifier = modifier,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

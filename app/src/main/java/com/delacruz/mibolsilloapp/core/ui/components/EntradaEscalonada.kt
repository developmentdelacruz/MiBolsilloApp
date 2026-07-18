package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_POR_ITEM_MS = 40L
private const val MAX_INDICE_CON_DELAY = 12
private const val OFFSET_INICIAL_PX = 40f

/**
 * Aparición escalonada para filas de una LazyColumn: fade + slide desde abajo, con un
 * delay proporcional a [indice] (capado en [MAX_INDICE_CON_DELAY] para que listas largas
 * no tarden en terminar de aparecer). Cubre la aparición INICIAL de la lista —
 * `Modifier.animateItem()` de Compose Foundation ya anima inserciones/borrados/
 * reordenamientos posteriores, pero no la primera composición.
 */
fun Modifier.entradaEscalonada(indice: Int): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(OFFSET_INICIAL_PX) }

    LaunchedEffect(Unit) {
        delay(indice.coerceAtMost(MAX_INDICE_CON_DELAY) * DELAY_POR_ITEM_MS)
        coroutineScope {
            launch { alpha.animateTo(1f, tween(300)) }
            launch { offsetY.animateTo(0f, tween(300)) }
        }
    }

    graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value
    }
}

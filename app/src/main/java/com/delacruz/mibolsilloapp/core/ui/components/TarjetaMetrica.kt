package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Card "hero" para el número principal de una pantalla (Balance, Disponible para gastar,
 * Patrimonio neto, resumen de Compromiso/Suscripción/Proyecto, etc.). Reemplaza las cards
 * resumen que antes se duplicaban a mano en cada pantalla con el mismo layout.
 *
 * [esHero] hace el valor más grande (headlineLarge vs headlineMedium) para marcar cuál es
 * EL número que importa en una pantalla con varias cards — jerarquía visual explícita en
 * vez de que todo se vea igual de importante.
 */
@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    esPositivo: Boolean? = null,
    subtitulo: String? = null,
    esHero: Boolean = false,
    colorContenedor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    colorContenido: Color = MaterialTheme.colorScheme.onSurface,
    contenidoExtra: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorContenedor, contentColor = colorContenido),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = titulo,
                style = if (esHero) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
            )
            MontoTexto(
                texto = valor,
                esPositivo = esPositivo,
                style = if (esHero) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (contenidoExtra != null) {
                Column(modifier = Modifier.padding(top = 12.dp)) { contenidoExtra() }
            }
        }
    }
}

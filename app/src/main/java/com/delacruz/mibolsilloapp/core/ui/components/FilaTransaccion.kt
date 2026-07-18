package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Fila de transacción: Card + ListItem con descripción, fecha y monto. Antes estaba
 * copiada pixel-idéntica en NegocioDetalleScreen y ProyectoDetalleScreen; se extrae acá
 * para no duplicarla (y de paso corrige que una de las dos copias usaba monto.toString()
 * en vez de formatearMonto).
 */
@Composable
fun FilaTransaccion(
    descripcion: String,
    fecha: String,
    montoFormateado: String,
    esPositivo: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        ListItem(
            headlineContent = { Text(descripcion) },
            supportingContent = { Text(fecha) },
            trailingContent = {
                MontoTexto(texto = montoFormateado, esPositivo = esPositivo)
            },
        )
    }
}

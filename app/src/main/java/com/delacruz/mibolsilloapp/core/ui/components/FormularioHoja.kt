package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Hoja inferior estándar para formularios de creación/edición: reemplaza el
 * AlertDialog genérico (más espacio, jerarquía visual más clara, patrón moderno).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioHoja(
    titulo: String,
    onCerrar: () -> Unit,
    onGuardar: () -> Unit,
    guardarHabilitado: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall)
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                contenido()
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onCerrar) { Text("Cancelar") }
                Button(
                    onClick = onGuardar,
                    enabled = guardarHabilitado,
                    modifier = Modifier.padding(start = 8.dp),
                ) { Text("Guardar") }
            }
        }
    }
}

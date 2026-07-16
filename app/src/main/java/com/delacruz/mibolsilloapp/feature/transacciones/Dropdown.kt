package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/** Dropdown genérico usado en los formularios de este feature (Tipo, Categoría, Negocio, Proyecto). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleDropdown(
    label: String,
    opciones: List<T>,
    seleccionado: T?,
    etiqueta: (T) -> String,
    onSeleccionar: (T?) -> Unit,
    permiteNinguno: Boolean = false,
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            readOnly = true,
            value = seleccionado?.let(etiqueta) ?: if (permiteNinguno) "Ninguno" else "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            if (permiteNinguno) {
                DropdownMenuItem(
                    text = { Text("Ninguno") },
                    onClick = {
                        onSeleccionar(null)
                        expandido = false
                    },
                )
            }
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(etiqueta(opcion)) },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    },
                )
            }
        }
    }
}

package com.delacruz.mibolsilloapp.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Campo de fecha con selector real (Material3 DatePicker), reemplaza el texto libre "AAAA-MM-DD". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaCampo(
    fecha: LocalDate,
    onFechaSeleccionada: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Fecha",
) {
    var mostrarSelector by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = fecha.toString(),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { mostrarSelector = true }) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Elegir fecha")
            }
        },
        modifier = modifier,
    )

    if (mostrarSelector) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { mostrarSelector = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { millis ->
                        onFechaSeleccionada(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mostrarSelector = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelector = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = estado)
        }
    }
}

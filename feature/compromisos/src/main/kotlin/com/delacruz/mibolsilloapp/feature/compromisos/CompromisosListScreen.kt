@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal

@Composable
fun CompromisosListScreen(
    viewModel: CompromisosListViewModel = hiltViewModel(),
    onCompromisoClick: (Long) -> Unit,
) {
    val compromisos by viewModel.compromisos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compromisos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(compromisos, key = { it.compromiso.id }) { item ->
                ListItem(
                    modifier = Modifier.clickable { onCompromisoClick(item.compromiso.id) },
                    headlineContent = { Text(item.compromiso.nombre) },
                    supportingContent = {
                        Text("Saldo: ${item.saldoPendiente} — Cuotas: ${item.cuotasPagadas}/${item.compromiso.cuotasTotales}")
                    },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }
        var cuotas by remember { mutableStateOf("") }
        var diaPago by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo compromiso") },
            text = {
                Column {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto total") })
                    OutlinedTextField(value = cuotas, onValueChange = { cuotas = it }, label = { Text("Cuotas totales") })
                    OutlinedTextField(
                        value = diaPago,
                        onValueChange = { diaPago = it },
                        label = { Text("Día de pago sugerido (1-31)") },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val montoBd = monto.toBigDecimalOrNull()
                    val cuotasInt = cuotas.toIntOrNull()
                    val diaInt = diaPago.toIntOrNull()
                    if (nombre.isNotBlank() && montoBd != null && cuotasInt != null && diaInt != null) {
                        viewModel.crear(nombre, montoBd, cuotasInt, diaInt)
                        mostrarDialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            },
        )
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}

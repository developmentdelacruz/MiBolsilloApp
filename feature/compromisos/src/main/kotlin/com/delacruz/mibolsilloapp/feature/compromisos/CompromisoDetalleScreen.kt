@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import java.time.LocalDate

@Composable
fun CompromisoDetalleScreen(viewModel: CompromisoDetalleViewModel = hiltViewModel()) {
    val detalle by viewModel.detalle.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(detalle?.compromiso?.nombre ?: "Compromiso") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            detalle?.let { info ->
                Text("Monto total: ${info.compromiso.montoTotal}")
                Text("Estado: ${info.compromiso.estado}")
            }
            LazyColumn {
                items(detalle?.pagos.orEmpty(), key = { it.id }) { pago ->
                    ListItem(
                        headlineContent = { Text("Cuota ${pago.numeroCuota}: ${pago.montoPagado}") },
                        supportingContent = {
                            Text(pago.fechaPagoReal.toString() + if (pago.esAdelantado) " (adelantado)" else "")
                        },
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        var monto by remember { mutableStateOf("") }
        var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
        var numeroCuota by remember { mutableStateOf("") }
        var esAdelantado by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Registrar pago") },
            text = {
                Column {
                    OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto pagado") })
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha (AAAA-MM-DD)") },
                    )
                    OutlinedTextField(
                        value = numeroCuota,
                        onValueChange = { numeroCuota = it },
                        label = { Text("Número de cuota") },
                    )
                    Row {
                        Checkbox(checked = esAdelantado, onCheckedChange = { esAdelantado = it })
                        Text("Abono adelantado / extraordinario")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
                    val fechaLd = runCatching { LocalDate.parse(fecha) }.getOrNull()
                    val cuotaInt = numeroCuota.toIntOrNull()
                    if (montoBd != null && fechaLd != null && cuotaInt != null) {
                        viewModel.registrarPago(montoBd, fechaLd, cuotaInt, esAdelantado)
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

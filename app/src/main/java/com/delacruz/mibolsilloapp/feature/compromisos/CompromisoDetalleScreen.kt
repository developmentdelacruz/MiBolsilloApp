@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.ChipEstado
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FechaCampo
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun CompromisoDetalleScreen(viewModel: CompromisoDetalleViewModel = hiltViewModel()) {
    val detalle by viewModel.detalle.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(detalle?.compromiso?.nombre ?: "Compromiso") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Registrar pago")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            detalle?.let { info ->
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "Monto total: ${info.compromiso.montoTotal.formatearMonto(simbolo)}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            ChipEstado(
                                texto = info.compromiso.estado.name,
                                tono = if (info.compromiso.estado == EstadoCompromiso.ACTIVO) {
                                    TonoChip.POSITIVO
                                } else {
                                    TonoChip.NEUTRO
                                },
                            )
                        }
                    }
                }
            }

            val pagos = detalle?.pagos.orEmpty()
            if (pagos.isEmpty()) {
                EstadoVacio(icono = Icons.Filled.Receipt, mensaje = "Todavía no registraste pagos")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(pagos, key = { it.id }) { pago ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text("Cuota ${pago.numeroCuota}", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        pago.fechaPagoReal.toString() + if (pago.esAdelantado) " (adelantado)" else "",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                MontoTexto(texto = pago.montoPagado.formatearMonto(simbolo), esPositivo = true)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        var monto by remember { mutableStateOf("") }
        var fecha by remember { mutableStateOf(LocalDate.now()) }
        var numeroCuota by remember { mutableStateOf("") }
        var esAdelantado by remember { mutableStateOf(false) }

        val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
        val cuotaInt = numeroCuota.toIntOrNull()
        val puedeGuardar = montoBd != null && cuotaInt != null

        FormularioHoja(
            titulo = "Registrar pago",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                if (puedeGuardar) {
                    viewModel.registrarPago(montoBd!!, fecha, cuotaInt!!, esAdelantado)
                    mostrarFormulario = false
                }
            },
        ) {
            OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto pagado") })
            FechaCampo(fecha = fecha, onFechaSeleccionada = { fecha = it })
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
    }
}

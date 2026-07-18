@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.ChipEstado
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FilaConSwipe
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.EstadoCompromiso
import java.math.BigDecimal

@Composable
fun CompromisosListScreen(
    viewModel: CompromisosListViewModel = hiltViewModel(),
    onCompromisoClick: (Long) -> Unit,
) {
    val compromisos by viewModel.compromisos.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Compromiso?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compromisos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo compromiso")
            }
        },
    ) { padding ->
        if (compromisos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Handshake,
                mensaje = "No tenés compromisos registrados",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(compromisos, key = { _, item -> item.compromiso.id }) { index, item ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(item.compromiso) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCompromisoClick(item.compromiso.id) },
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(item.compromiso.nombre, style = MaterialTheme.typography.titleMedium)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        ChipEstado(
                                            texto = item.compromiso.estado.name,
                                            tono = if (item.compromiso.estado == EstadoCompromiso.ACTIVO) {
                                                TonoChip.POSITIVO
                                            } else {
                                                TonoChip.NEUTRO
                                            },
                                        )
                                        IconButton(onClick = { elementoEnEdicion = item.compromiso }) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Editar compromiso")
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        "Cuotas: ${item.cuotasPagadas}/${item.compromiso.cuotasTotales}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    MontoTexto(
                                        texto = "Saldo: ${item.saldoPendiente.formatearMonto(simbolo)}",
                                        esPositivo = item.saldoPendiente <= BigDecimal.ZERO,
                                    )
                                }
                                val progresoObjetivo = if (item.compromiso.cuotasTotales > 0) {
                                    item.cuotasPagadas.toFloat() / item.compromiso.cuotasTotales.toFloat()
                                } else {
                                    0f
                                }
                                val progreso by animateFloatAsState(
                                    targetValue = progresoObjetivo.coerceIn(0f, 1f),
                                    label = "progresoCuotas",
                                )
                                LinearProgressIndicator(
                                    progress = { progreso },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario || elementoEnEdicion != null) {
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        var monto by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.montoTotal?.toPlainString() ?: "") }
        var cuotas by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.cuotasTotales?.toString() ?: "") }
        var diaPago by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.diaPagoSugerido?.toString() ?: "") }

        val montoBd = monto.toBigDecimalOrNull()
        val cuotasInt = cuotas.toIntOrNull()
        val diaInt = diaPago.toIntOrNull()
        val puedeGuardar = nombre.isNotBlank() && montoBd != null && cuotasInt != null && diaInt != null

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar compromiso" else "Nuevo compromiso",
            onCerrar = {
                mostrarFormulario = false
                elementoEnEdicion = null
            },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val compromisoEditado = elementoEnEdicion
                if (puedeGuardar) {
                    if (compromisoEditado != null) {
                        viewModel.actualizar(
                            compromisoEditado.copy(
                                nombre = nombre,
                                montoTotal = montoBd!!,
                                cuotasTotales = cuotasInt!!,
                                diaPagoSugerido = diaInt!!,
                            ),
                        )
                    } else {
                        viewModel.crear(nombre, montoBd!!, cuotasInt!!, diaInt!!)
                    }
                    mostrarFormulario = false
                    elementoEnEdicion = null
                }
            },
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto total") })
            OutlinedTextField(value = cuotas, onValueChange = { cuotas = it }, label = { Text("Cuotas totales") })
            OutlinedTextField(
                value = diaPago,
                onValueChange = { diaPago = it },
                label = { Text("Día de pago sugerido (1-31)") },
            )
        }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}

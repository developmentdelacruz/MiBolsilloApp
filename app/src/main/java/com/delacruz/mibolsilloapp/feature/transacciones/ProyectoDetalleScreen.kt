@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import java.math.BigDecimal

@Composable
fun ProyectoDetalleScreen(viewModel: ProyectoDetalleViewModel = hiltViewModel()) {
    val proyecto by viewModel.proyecto.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(proyecto?.proyecto?.nombre ?: "Proyecto") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            proyecto?.let { info ->
                val presupuesto = info.proyecto.presupuestoEstimado
                val progreso = if (presupuesto > BigDecimal.ZERO) {
                    (info.costoAcumulado.toDouble() / presupuesto.toDouble()).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }
                val excedido = info.costoAcumulado > presupuesto

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Presupuesto: $presupuesto")
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        color = if (excedido) {
                            MiBolsilloTheme.extendedColors.negative
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    )
                    MontoTexto(texto = "Gastado: ${info.costoAcumulado}", esPositivo = false)
                    MontoTexto(
                        texto = "Restante: ${info.presupuestoRestante}",
                        esPositivo = info.presupuestoRestante.signum() >= 0,
                    )
                }
            }
            if (transacciones.isEmpty()) {
                EstadoVacio(
                    icono = Icons.Filled.Receipt,
                    mensaje = "Este proyecto todavía no tiene transacciones.",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(transacciones, key = { it.id }) { transaccion ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            ListItem(
                                headlineContent = { Text(transaccion.descripcion) },
                                supportingContent = { Text("${transaccion.fecha}") },
                                trailingContent = {
                                    MontoTexto(
                                        texto = transaccion.monto.toString(),
                                        esPositivo = transaccion.tipo == TipoTransaccion.INGRESO,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

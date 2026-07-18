@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.LinearProgressIndicator
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
import com.delacruz.mibolsilloapp.core.ui.components.FilaTransaccion
import com.delacruz.mibolsilloapp.core.ui.components.TarjetaMetrica
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import java.math.BigDecimal

@Composable
fun ProyectoDetalleScreen(viewModel: ProyectoDetalleViewModel = hiltViewModel()) {
    val proyecto by viewModel.proyecto.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(proyecto?.proyecto?.nombre ?: "Proyecto") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            proyecto?.let { info ->
                val presupuesto = info.proyecto.presupuestoEstimado
                val progresoObjetivo = if (presupuesto > BigDecimal.ZERO) {
                    (info.costoAcumulado.toDouble() / presupuesto.toDouble()).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }
                val progresoAnimado by animateFloatAsState(targetValue = progresoObjetivo, label = "progresoProyecto")
                val excedido = info.costoAcumulado > presupuesto

                TarjetaMetrica(
                    titulo = "Restante",
                    valor = info.presupuestoRestante.formatearMonto(simbolo),
                    esPositivo = info.presupuestoRestante.signum() >= 0,
                    esHero = true,
                    subtitulo = "Gastado: ${info.costoAcumulado.formatearMonto(simbolo)} " +
                        "de ${presupuesto.formatearMonto(simbolo)}",
                    modifier = Modifier.padding(16.dp),
                    contenidoExtra = {
                        LinearProgressIndicator(
                            progress = { progresoAnimado },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (excedido) {
                                MiBolsilloTheme.extendedColors.negative
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        )
                    },
                )
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
                    itemsIndexed(transacciones, key = { _, t -> t.id }) { index, transaccion ->
                        FilaTransaccion(
                            descripcion = transaccion.descripcion,
                            fecha = "${transaccion.fecha}",
                            montoFormateado = transaccion.monto.formatearMonto(simbolo),
                            esPositivo = transaccion.tipo == TipoTransaccion.INGRESO,
                            modifier = Modifier.entradaEscalonada(index),
                        )
                    }
                }
            }
        }
    }
}

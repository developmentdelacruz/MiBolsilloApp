@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
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
import com.delacruz.mibolsilloapp.core.ui.components.LineChart
import com.delacruz.mibolsilloapp.core.ui.components.TarjetaMetrica
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import java.math.BigDecimal

@Composable
fun PresupuestoDetalleScreen(viewModel: PresupuestoDetalleViewModel = hiltViewModel()) {
    val presupuesto by viewModel.presupuesto.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(presupuesto?.categoria?.nombre ?: "Presupuesto") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            presupuesto?.let { item ->
                val progresoAnimado by animateFloatAsState(
                    targetValue = item.porcentajeConsumido,
                    label = "progresoPresupuestoDetalle",
                )
                val excedido = item.porcentajeConsumido >= 1f

                TarjetaMetrica(
                    titulo = "Consumido este mes",
                    valor = item.consumido.formatearMonto(simbolo),
                    esPositivo = !excedido,
                    esHero = true,
                    subtitulo = "de ${item.presupuesto.montoMensual.formatearMonto(simbolo)} presupuestados",
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

            Text(
                "Historial (últimos 12 meses)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )

            if (historial.size < 2) {
                EstadoVacio(
                    icono = Icons.Filled.ShowChart,
                    mensaje = "Todavía no hay suficiente historial de esta categoría para graficar.",
                )
            } else {
                LineChart(
                    valores = historial.map { it.monto.toFloat() },
                    lineaReferencia = presupuesto?.presupuesto?.montoMensual?.toFloat(),
                    formatoValor = { v -> BigDecimal(v.toString()).formatearMonto(simbolo) },
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(historial.first().mes.toString(), style = MaterialTheme.typography.bodySmall)
                    Text(historial.last().mes.toString(), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

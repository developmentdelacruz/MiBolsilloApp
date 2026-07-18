@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.gastoscompartidos

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
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.ChipEstado
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import java.math.BigDecimal

@Composable
fun GastosCompartidosScreen(viewModel: GastosCompartidosViewModel = hiltViewModel()) {
    val gastos by viewModel.gastos.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()

    val grupos = remember(gastos) {
        gastos
            .groupBy { it.gasto.nombreContacto to it.gasto.telefono }
            .map { (contacto, gastosDelContacto) ->
                val pendiente = gastosDelContacto
                    .filter { it.gasto.estadoPago == EstadoPago.PENDIENTE }
                    .fold(BigDecimal.ZERO) { acumulado, item -> acumulado + item.gasto.montoAPagar }
                Triple(contacto.first, pendiente, gastosDelContacto)
            }
            .sortedByDescending { it.second }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gastos compartidos") }) },
    ) { padding ->
        if (gastos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.People,
                mensaje = "Todavía no dividiste ningún gasto con nadie.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                grupos.forEach { (nombreContacto, pendiente, gastosDelContacto) ->
                    item {
                        Column {
                            Text(nombreContacto, style = MaterialTheme.typography.titleMedium)
                            MontoTexto(
                                texto = if (pendiente.signum() > 0) {
                                    "Te debe ${pendiente.formatearMonto(simbolo)} (${gastosDelContacto.count { it.gasto.estadoPago == EstadoPago.PENDIENTE }} gastos)"
                                } else {
                                    "Sin pendientes"
                                },
                                esPositivo = pendiente.signum() == 0,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                    itemsIndexed(
                        gastosDelContacto,
                        key = { _, item -> item.gasto.id },
                    ) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().entradaEscalonada(index),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(item.descripcionTransaccion, style = MaterialTheme.typography.bodyMedium)
                                    ChipEstado(
                                        texto = item.gasto.estadoPago.name,
                                        tono = if (item.gasto.estadoPago == EstadoPago.PAGADO) {
                                            TonoChip.POSITIVO
                                        } else {
                                            TonoChip.NEGATIVO
                                        },
                                    )
                                }
                                Text(
                                    "${item.fechaTransaccion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                MontoTexto(
                                    texto = item.gasto.montoAPagar.formatearMonto(simbolo),
                                    esPositivo = item.gasto.estadoPago == EstadoPago.PAGADO,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                                if (item.gasto.estadoPago == EstadoPago.PENDIENTE) {
                                    TextButton(onClick = { viewModel.marcarPagado(item.gasto) }) {
                                        Text("Marcar pagado")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

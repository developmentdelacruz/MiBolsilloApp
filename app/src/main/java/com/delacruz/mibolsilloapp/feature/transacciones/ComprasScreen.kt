@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FechaCampo
import com.delacruz.mibolsilloapp.core.ui.components.FilaConSwipe
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Compra
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.Negocio
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun ComprasScreen(viewModel: ComprasViewModel = hiltViewModel()) {
    val compras by viewModel.compras.collectAsState()
    val categorias by viewModel.categoriasDeGasto.collectAsState()
    val cuentas by viewModel.cuentas.collectAsState()
    val negocios by viewModel.negocios.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Compra?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compras en cuotas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva compra en cuotas")
            }
        },
    ) { padding ->
        if (compras.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.CreditCard,
                mensaje = "Todavía no registraste ninguna compra en cuotas.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(compras, key = { _, compra -> compra.id }) { index, compra ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(compra) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { elementoEnEdicion = compra },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            ListItem(
                                headlineContent = { Text(compra.descripcion) },
                                supportingContent = {
                                    Text("${compra.cuotasTotales} cuotas desde ${compra.fechaPrimeraCuota}")
                                },
                                trailingContent = {
                                    Text(compra.montoTotal.formatearMonto(simbolo))
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario || elementoEnEdicion != null) {
        val edicion = elementoEnEdicion
        var descripcion by remember(edicion) { mutableStateOf(edicion?.descripcion ?: "") }
        var montoTotal by remember(edicion) { mutableStateOf(edicion?.montoTotal?.toPlainString() ?: "") }
        var cuotasTotales by remember(edicion) { mutableStateOf(edicion?.cuotasTotales?.toString() ?: "") }
        var fechaPrimeraCuota by remember(edicion) { mutableStateOf(edicion?.fechaPrimeraCuota ?: LocalDate.now()) }
        var categoria by remember(edicion) {
            mutableStateOf(categorias.find { it.id == edicion?.categoriaId })
        }
        var cuenta by remember(edicion) {
            mutableStateOf(cuentas.find { it.id == edicion?.cuentaId })
        }
        var negocio by remember(edicion) {
            mutableStateOf(negocios.find { it.id == edicion?.negocioId })
        }

        val montoBd = runCatching { BigDecimal(montoTotal) }.getOrNull()
        val cuotasInt = cuotasTotales.toIntOrNull()
        val puedeGuardar = descripcion.isNotBlank() &&
            categoria != null &&
            cuenta != null &&
            (edicion != null || (montoBd != null && cuotasInt != null && cuotasInt in 2..60))

        FormularioHoja(
            titulo = if (edicion != null) "Editar compra en cuotas" else "Nueva compra en cuotas",
            onCerrar = {
                mostrarFormulario = false
                elementoEnEdicion = null
            },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val categoriaSeleccionada = categoria
                val cuentaSeleccionada = cuenta
                if (edicion != null) {
                    if (puedeGuardar && categoriaSeleccionada != null && cuentaSeleccionada != null) {
                        viewModel.actualizar(
                            edicion.copy(
                                descripcion = descripcion,
                                categoriaId = categoriaSeleccionada.id,
                                cuentaId = cuentaSeleccionada.id,
                                negocioId = negocio?.id,
                            ),
                        )
                        elementoEnEdicion = null
                    }
                } else if (puedeGuardar && categoriaSeleccionada != null && cuentaSeleccionada != null &&
                    montoBd != null && cuotasInt != null
                ) {
                    viewModel.crear(
                        descripcion = descripcion,
                        montoTotal = montoBd,
                        cuotasTotales = cuotasInt,
                        categoriaId = categoriaSeleccionada.id,
                        cuentaId = cuentaSeleccionada.id,
                        negocioId = negocio?.id,
                        fechaPrimeraCuota = fechaPrimeraCuota,
                    )
                    mostrarFormulario = false
                }
            },
        ) {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
            )
            if (edicion != null) {
                OutlinedTextField(
                    value = montoTotal,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Monto total") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = cuotasTotales,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Cantidad de cuotas") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = fechaPrimeraCuota.toString(),
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Primera cuota") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "El monto, la cantidad de cuotas y la fecha no se pueden editar: ya generaron " +
                        "las transacciones de cada cuota.",
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                OutlinedTextField(
                    value = montoTotal,
                    onValueChange = { montoTotal = it },
                    label = { Text("Monto total") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = cuotasTotales,
                    onValueChange = { cuotasTotales = it },
                    label = { Text("Cantidad de cuotas (2-60)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                FechaCampo(
                    fecha = fechaPrimeraCuota,
                    onFechaSeleccionada = { fechaPrimeraCuota = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            SimpleDropdown(
                label = "Categoría",
                opciones = categorias,
                seleccionado = categoria,
                etiqueta = { it.nombre },
                onSeleccionar = { categoria = it },
            )
            SimpleDropdown(
                label = "Cuenta",
                opciones = cuentas,
                seleccionado = cuenta,
                etiqueta = { it.nombre },
                onSeleccionar = { cuenta = it },
            )
            SimpleDropdown(
                label = "Negocio",
                opciones = negocios,
                seleccionado = negocio,
                etiqueta = { it.nombre },
                onSeleccionar = { negocio = it },
                permiteNinguno = true,
            )
        }
    }
}

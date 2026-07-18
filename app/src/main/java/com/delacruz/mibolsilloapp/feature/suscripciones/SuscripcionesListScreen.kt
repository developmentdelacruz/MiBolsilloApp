@file:OptIn(ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.suscripciones

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
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import com.delacruz.mibolsilloapp.core.ui.components.FilaConSwipe
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.IconoCategoria
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import java.math.BigDecimal

@Composable
fun SuscripcionesListScreen(
    viewModel: SuscripcionesListViewModel = hiltViewModel(),
    onSuscripcionClick: (Long) -> Unit,
) {
    val suscripciones by viewModel.suscripciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Suscripcion?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Suscripciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva suscripción")
            }
        },
    ) { padding ->
        if (suscripciones.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Subscriptions,
                mensaje = "No tenés suscripciones registradas",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(suscripciones, key = { _, item -> item.id }) { index, suscripcion ->
                    val categoria = categorias.firstOrNull { it.id == suscripcion.categoriaId }
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(suscripcion) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuscripcionClick(suscripcion.id) },
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row {
                                        IconoCategoria(
                                            icono = categoria?.icono ?: "💳",
                                            modifier = Modifier.padding(end = 12.dp),
                                        )
                                        Text(suscripcion.nombre, style = MaterialTheme.typography.titleMedium)
                                    }
                                    Row {
                                        MontoTexto(
                                            texto = "${suscripcion.montoMensual.formatearMonto(simbolo)}/mes",
                                            esPositivo = false,
                                        )
                                        IconButton(onClick = { elementoEnEdicion = suscripcion }) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Editar suscripción")
                                        }
                                    }
                                }
                                Text(
                                    "Día de cobro: ${suscripcion.diaCobro}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp),
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
        var monto by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.montoMensual?.toPlainString() ?: "")
        }
        var diaCobro by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.diaCobro?.toString() ?: "")
        }
        var categoriaSeleccionada by remember(elementoEnEdicion) {
            mutableStateOf(categorias.firstOrNull { it.id == elementoEnEdicion?.categoriaId })
        }
        var expandido by remember { mutableStateOf(false) }

        val montoBd = monto.toBigDecimalOrNull()
        val diaInt = diaCobro.toIntOrNull()
        val puedeGuardar = nombre.isNotBlank() && montoBd != null && diaInt != null && categoriaSeleccionada != null

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar suscripción" else "Nueva suscripción",
            onCerrar = {
                mostrarFormulario = false
                elementoEnEdicion = null
            },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val edicion = elementoEnEdicion
                if (edicion != null) {
                    viewModel.actualizar(
                        edicion.copy(
                            nombre = nombre,
                            montoMensual = montoBd!!,
                            diaCobro = diaInt!!,
                            categoriaId = categoriaSeleccionada!!.id,
                        ),
                    )
                } else {
                    viewModel.crear(nombre, montoBd!!, diaInt!!, categoriaSeleccionada!!.id)
                }
                mostrarFormulario = false
                elementoEnEdicion = null
            },
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto mensual") })
            OutlinedTextField(
                value = diaCobro,
                onValueChange = { diaCobro = it },
                label = { Text("Día de cobro (1-31)") },
            )
            ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    readOnly = true,
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                )
                ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expandido = false
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}

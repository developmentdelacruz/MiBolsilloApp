@file:OptIn(ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.IconoCategoria
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import java.math.BigDecimal

@Composable
fun CatalogosScreen() {
    var tabSeleccionado by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogos") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabSeleccionado) {
                Tab(selected = tabSeleccionado == 0, onClick = { tabSeleccionado = 0 }, text = { Text("Categorías") })
                Tab(selected = tabSeleccionado == 1, onClick = { tabSeleccionado = 1 }, text = { Text("Monedas") })
                Tab(selected = tabSeleccionado == 2, onClick = { tabSeleccionado = 2 }, text = { Text("Presupuestos") })
            }
            when (tabSeleccionado) {
                0 -> CategoriasTab()
                1 -> MonedasTab()
                else -> PresupuestosTab()
            }
        }
    }
}

/** Envuelve una fila en swipe-to-delete: desliza en cualquier dirección para eliminar. */
@Composable
private fun FilaConSwipe(
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit,
) {
    val estado = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor != SwipeToDismissBoxValue.Settled) {
                onEliminar()
            }
            true
        },
    )
    SwipeToDismissBox(
        state = estado,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        contenido()
    }
}

@Composable
private fun CategoriasTab(viewModel: CategoriasViewModel = hiltViewModel()) {
    val categorias by viewModel.categorias.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva categoría")
            }
        },
    ) { padding ->
        if (categorias.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Category,
                mensaje = "No tenés categorías registradas",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categorias, key = { it.id }) { categoria ->
                    FilaConSwipe(onEliminar = { viewModel.eliminar(categoria) }) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconoCategoria(categoria.icono, modifier = Modifier.padding(end = 12.dp))
                                Column {
                                    Text(categoria.nombre, style = MaterialTheme.typography.titleMedium)
                                    ChipEstado(
                                        texto = categoria.tipo.name,
                                        tono = if (categoria.tipo == TipoCategoria.INGRESO) {
                                            TonoChip.POSITIVO
                                        } else {
                                            TonoChip.NEGATIVO
                                        },
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var icono by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf(TipoCategoria.GASTO) }
        var expandido by remember { mutableStateOf(false) }

        FormularioHoja(
            titulo = "Nueva categoría",
            onCerrar = { mostrarDialogo = false },
            onGuardar = {
                viewModel.crear(nombre, icono.ifBlank { "🏷️" }, tipo)
                mostrarDialogo = false
            },
            guardarHabilitado = nombre.isNotBlank(),
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = icono,
                onValueChange = { icono = it },
                label = { Text("Icono (emoji)") },
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    readOnly = true,
                    value = tipo.name,
                    onValueChange = {},
                    label = { Text("Tipo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                )
                ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    TipoCategoria.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.name) },
                            onClick = {
                                tipo = opcion
                                expandido = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonedasTab(viewModel: MonedasViewModel = hiltViewModel()) {
    val monedas by viewModel.monedas.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva moneda")
            }
        },
    ) { padding ->
        if (monedas.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.AttachMoney,
                mensaje = "No tenés monedas registradas",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(monedas, key = { it.id }) { moneda ->
                    FilaConSwipe(onEliminar = { viewModel.eliminar(moneda) }) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(
                                    "${moneda.simbolo}  ${moneda.codigo} — ${moneda.nombre}",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                if (moneda.esPredeterminada) {
                                    ChipEstado(
                                        texto = "Predeterminada",
                                        tono = TonoChip.POSITIVO,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var codigo by remember { mutableStateOf("") }
        var nombre by remember { mutableStateOf("") }
        var simbolo by remember { mutableStateOf("") }
        var predeterminada by remember { mutableStateOf(false) }

        FormularioHoja(
            titulo = "Nueva moneda",
            onCerrar = { mostrarDialogo = false },
            onGuardar = {
                viewModel.crear(codigo, nombre, simbolo, predeterminada)
                mostrarDialogo = false
            },
            guardarHabilitado = codigo.isNotBlank() && simbolo.isNotBlank(),
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código (GTQ, USD...)") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = simbolo,
                onValueChange = { simbolo = it },
                label = { Text("Símbolo (Q, $)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = predeterminada, onCheckedChange = { predeterminada = it })
                Text("Predeterminada")
            }
        }
    }
}

@Composable
private fun PresupuestosTab(viewModel: PresupuestosViewModel = hiltViewModel()) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val categoriasDisponibles by viewModel.categoriasDisponibles.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo presupuesto")
            }
        },
    ) { padding ->
        if (presupuestos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.PieChart,
                mensaje = "No tenés presupuestos asignados todavía",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(presupuestos, key = { it.presupuesto.id }) { item ->
                    FilaConSwipe(onEliminar = { viewModel.eliminar(item.presupuesto) }) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                IconoCategoria(item.categoria.icono, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(item.categoria.nombre, style = MaterialTheme.typography.titleMedium)
                                    LinearProgressIndicator(
                                        progress = { item.porcentajeConsumido },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        color = if (item.porcentajeConsumido >= 1f) {
                                            MiBolsilloTheme.extendedColors.negative
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                    )
                                    MontoTexto(
                                        texto = "Restante: ${item.restante.formatearMonto(simbolo)}",
                                        esPositivo = item.restante.signum() >= 0,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var montoMensual by remember { mutableStateOf("") }
        var categoriaId by remember { mutableStateOf<Long?>(categoriasDisponibles.firstOrNull()?.id) }
        var expandido by remember { mutableStateOf(false) }
        val categoriaSeleccionada = categoriasDisponibles.firstOrNull { it.id == categoriaId }

        FormularioHoja(
            titulo = "Nuevo presupuesto",
            onCerrar = { mostrarDialogo = false },
            onGuardar = {
                val montoBd = runCatching { BigDecimal(montoMensual) }.getOrNull()
                val categoriaSeleccionadaId = categoriaId
                if (montoBd != null && categoriaSeleccionadaId != null) {
                    viewModel.crear(categoriaSeleccionadaId, montoBd)
                    mostrarDialogo = false
                }
            },
            guardarHabilitado = categoriasDisponibles.isNotEmpty() &&
                categoriaId != null &&
                montoMensual.toBigDecimalOrNull() != null,
        ) {
            if (categoriasDisponibles.isEmpty()) {
                Text("Todas tus categorías de gasto ya tienen un presupuesto asignado.")
            } else {
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
                        categoriasDisponibles.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.nombre) },
                                onClick = {
                                    categoriaId = opcion.id
                                    expandido = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = montoMensual,
                    onValueChange = { montoMensual = it },
                    label = { Text("Monto mensual") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }
        }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}

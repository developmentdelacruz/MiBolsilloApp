@file:OptIn(ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.ChipEstado
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FilaConSwipe
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.IconoCategoria
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.model.Presupuesto
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria
import java.math.BigDecimal

@Composable
fun CatalogosScreen(onPresupuestoClick: (Long) -> Unit) {
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
            AnimatedContent(
                targetState = tabSeleccionado,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tabCatalogos",
            ) { tab ->
                when (tab) {
                    0 -> CategoriasTab()
                    1 -> MonedasTab()
                    else -> PresupuestosTab(onPresupuestoClick = onPresupuestoClick)
                }
            }
        }
    }
}

@Composable
private fun CategoriasTab(viewModel: CategoriasViewModel = hiltViewModel()) {
    val categorias by viewModel.categorias.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Categoria?>(null) }

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
                itemsIndexed(categorias, key = { _, categoria -> categoria.id }) { index, categoria ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(categoria) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { elementoEnEdicion = categoria },
                        ) {
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

    if (mostrarDialogo || elementoEnEdicion != null) {
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        var icono by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.icono ?: "") }
        var tipo by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.tipo ?: TipoCategoria.GASTO) }
        var expandido by remember { mutableStateOf(false) }

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar categoría" else "Nueva categoría",
            onCerrar = {
                mostrarDialogo = false
                elementoEnEdicion = null
            },
            onGuardar = {
                val categoriaEnEdicion = elementoEnEdicion
                if (categoriaEnEdicion != null) {
                    viewModel.actualizar(
                        categoriaEnEdicion.copy(nombre = nombre, icono = icono.ifBlank { "🏷️" }, tipo = tipo),
                    )
                } else {
                    viewModel.crear(nombre, icono.ifBlank { "🏷️" }, tipo)
                }
                mostrarDialogo = false
                elementoEnEdicion = null
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
    var elementoEnEdicion by remember { mutableStateOf<Moneda?>(null) }

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
                itemsIndexed(monedas, key = { _, moneda -> moneda.id }) { index, moneda ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(moneda) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { elementoEnEdicion = moneda },
                        ) {
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

    if (mostrarDialogo || elementoEnEdicion != null) {
        var codigo by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.codigo ?: "") }
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        var simbolo by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.simbolo ?: "") }
        var predeterminada by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.esPredeterminada ?: false)
        }

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar moneda" else "Nueva moneda",
            onCerrar = {
                mostrarDialogo = false
                elementoEnEdicion = null
            },
            onGuardar = {
                val monedaEnEdicion = elementoEnEdicion
                if (monedaEnEdicion != null) {
                    viewModel.actualizar(
                        monedaEnEdicion.copy(
                            codigo = codigo,
                            nombre = nombre,
                            simbolo = simbolo,
                            esPredeterminada = predeterminada,
                        ),
                    )
                } else {
                    viewModel.crear(codigo, nombre, simbolo, predeterminada)
                }
                mostrarDialogo = false
                elementoEnEdicion = null
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
private fun PresupuestosTab(
    onPresupuestoClick: (Long) -> Unit,
    viewModel: PresupuestosViewModel = hiltViewModel(),
) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val categoriasDisponibles by viewModel.categoriasDisponibles.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<PresupuestoConConsumo?>(null) }

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
                itemsIndexed(presupuestos, key = { _, item -> item.presupuesto.id }) { index, item ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(item.presupuesto) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (item.presupuesto.activo) 1f else 0.5f)
                                .clickable { elementoEnEdicion = item },
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                IconoCategoria(item.categoria.icono, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.categoria.nombre, style = MaterialTheme.typography.titleMedium)
                                        if (!item.presupuesto.activo) {
                                            Text(
                                                "· Inactivo",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 6.dp),
                                            )
                                        }
                                    }
                                    val progresoAnimado by animateFloatAsState(
                                        targetValue = item.porcentajeConsumido,
                                        label = "progresoPresupuesto",
                                    )
                                    LinearProgressIndicator(
                                        progress = { progresoAnimado },
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
                                    if (item.rolloverAcumulado.signum() != 0) {
                                        Text(
                                            "Disponible: ${item.disponibleTotal.formatearMonto(simbolo)} " +
                                                "(incluye ${item.rolloverAcumulado.formatearMonto(simbolo)} " +
                                                "de meses anteriores)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp),
                                        )
                                    }
                                }
                                IconButton(onClick = { onPresupuestoClick(item.presupuesto.categoriaId) }) {
                                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Ver historial")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo || elementoEnEdicion != null) {
        val presupuestoEnEdicion = elementoEnEdicion
        // En edición, la categoría ya asignada no aparece en categoriasDisponibles (está excluida
        // por tener presupuesto propio) — hay que reinyectarla para poder mostrarla seleccionada.
        val categoriasParaFormulario = remember(categoriasDisponibles, presupuestoEnEdicion) {
            if (presupuestoEnEdicion != null &&
                categoriasDisponibles.none { it.id == presupuestoEnEdicion.categoria.id }
            ) {
                categoriasDisponibles + presupuestoEnEdicion.categoria
            } else {
                categoriasDisponibles
            }
        }
        var montoMensual by remember(elementoEnEdicion) {
            mutableStateOf(presupuestoEnEdicion?.presupuesto?.montoMensual?.toPlainString() ?: "")
        }
        var categoriaId by remember(elementoEnEdicion) {
            mutableStateOf(presupuestoEnEdicion?.categoria?.id ?: categoriasParaFormulario.firstOrNull()?.id)
        }
        var activo by remember(elementoEnEdicion) {
            mutableStateOf(presupuestoEnEdicion?.presupuesto?.activo ?: true)
        }
        var expandido by remember { mutableStateOf(false) }
        val categoriaSeleccionada = categoriasParaFormulario.firstOrNull { it.id == categoriaId }

        FormularioHoja(
            titulo = if (presupuestoEnEdicion != null) "Editar presupuesto" else "Nuevo presupuesto",
            onCerrar = {
                mostrarDialogo = false
                elementoEnEdicion = null
            },
            onGuardar = {
                val montoBd = runCatching { BigDecimal(montoMensual) }.getOrNull()
                val categoriaSeleccionadaId = categoriaId
                if (montoBd != null && categoriaSeleccionadaId != null) {
                    if (presupuestoEnEdicion != null) {
                        viewModel.actualizar(
                            presupuestoEnEdicion.presupuesto.copy(
                                categoriaId = categoriaSeleccionadaId,
                                montoMensual = montoBd,
                                activo = activo,
                            ),
                        )
                    } else {
                        viewModel.crear(categoriaSeleccionadaId, montoBd)
                    }
                    mostrarDialogo = false
                    elementoEnEdicion = null
                }
            },
            guardarHabilitado = categoriasParaFormulario.isNotEmpty() &&
                categoriaId != null &&
                montoMensual.toBigDecimalOrNull() != null,
        ) {
            if (categoriasParaFormulario.isEmpty()) {
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
                        categoriasParaFormulario.forEach { opcion ->
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
                if (presupuestoEnEdicion != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Presupuesto activo")
                        Switch(checked = activo, onCheckedChange = { activo = it })
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

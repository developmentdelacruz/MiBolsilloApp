@file:OptIn(ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria

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
            }
            if (tabSeleccionado == 0) CategoriasTab() else MonedasTab()
        }
    }
}

@Composable
private fun CategoriasTab(viewModel: CategoriasViewModel = hiltViewModel()) {
    val categorias by viewModel.categorias.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(categorias, key = { it.id }) { categoria ->
                ListItem(
                    headlineContent = { Text("${categoria.icono}  ${categoria.nombre}") },
                    supportingContent = { Text(categoria.tipo.name) },
                    trailingContent = {
                        TextButton(onClick = { viewModel.eliminar(categoria) }) { Text("Eliminar") }
                    },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var icono by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf(TipoCategoria.GASTO) }
        var expandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva categoría") },
            text = {
                Column {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(
                        value = icono,
                        onValueChange = { icono = it },
                        label = { Text("Icono (emoji)") },
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
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nombre.isNotBlank()) {
                        viewModel.crear(nombre, icono.ifBlank { "🏷️" }, tipo)
                        mostrarDialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun MonedasTab(viewModel: MonedasViewModel = hiltViewModel()) {
    val monedas by viewModel.monedas.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(monedas, key = { it.id }) { moneda ->
                ListItem(
                    headlineContent = { Text("${moneda.simbolo}  ${moneda.codigo} — ${moneda.nombre}") },
                    supportingContent = { if (moneda.esPredeterminada) Text("Predeterminada") },
                    trailingContent = {
                        TextButton(onClick = { viewModel.eliminar(moneda) }) { Text("Eliminar") }
                    },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var codigo by remember { mutableStateOf("") }
        var nombre by remember { mutableStateOf("") }
        var simbolo by remember { mutableStateOf("") }
        var predeterminada by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva moneda") },
            text = {
                Column {
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código (GTQ, USD...)") },
                    )
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(
                        value = simbolo,
                        onValueChange = { simbolo = it },
                        label = { Text("Símbolo (Q, $)") },
                    )
                    Row {
                        Checkbox(checked = predeterminada, onCheckedChange = { predeterminada = it })
                        Text("Predeterminada")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (codigo.isNotBlank() && simbolo.isNotBlank()) {
                        viewModel.crear(codigo, nombre, simbolo, predeterminada)
                        mostrarDialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            },
        )
    }
}

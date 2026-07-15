@file:OptIn(ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.suscripciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.delacruz.mibolsilloapp.domain.model.Categoria
import java.math.BigDecimal

@Composable
fun SuscripcionesListScreen(
    viewModel: SuscripcionesListViewModel = hiltViewModel(),
    onSuscripcionClick: (Long) -> Unit,
) {
    val suscripciones by viewModel.suscripciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Suscripciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(suscripciones, key = { it.id }) { suscripcion ->
                ListItem(
                    modifier = Modifier.clickable { onSuscripcionClick(suscripcion.id) },
                    headlineContent = { Text(suscripcion.nombre) },
                    supportingContent = {
                        Text("${suscripcion.montoMensual}/mes — día ${suscripcion.diaCobro}")
                    },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }
        var diaCobro by remember { mutableStateOf("") }
        var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
        var expandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva suscripción") },
            text = {
                Column {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto mensual") },
                    )
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
            },
            confirmButton = {
                TextButton(onClick = {
                    val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
                    val diaInt = diaCobro.toIntOrNull()
                    val categoria = categoriaSeleccionada
                    if (nombre.isNotBlank() && montoBd != null && diaInt != null && categoria != null) {
                        viewModel.crear(nombre, montoBd, diaInt, categoria.id)
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

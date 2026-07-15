@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
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
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun TransaccionesScreen(
    onNegocioClick: (Long) -> Unit,
    onProyectoClick: (Long) -> Unit,
) {
    var tabSeleccionado by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Flujo de caja") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabSeleccionado) {
                Tab(selected = tabSeleccionado == 0, onClick = { tabSeleccionado = 0 }, text = { Text("Transacciones") })
                Tab(selected = tabSeleccionado == 1, onClick = { tabSeleccionado = 1 }, text = { Text("Negocios") })
                Tab(selected = tabSeleccionado == 2, onClick = { tabSeleccionado = 2 }, text = { Text("Proyectos") })
            }
            when (tabSeleccionado) {
                0 -> FlujoDeCajaTab()
                1 -> NegociosTab(onNegocioClick = onNegocioClick)
                else -> ProyectosTab(onProyectoClick = onProyectoClick)
            }
        }
    }
}

@Composable
private fun FlujoDeCajaTab(viewModel: TransaccionesViewModel = hiltViewModel()) {
    val transacciones by viewModel.transacciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val negocios by viewModel.negocios.collectAsState()
    val proyectos by viewModel.proyectos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(transacciones, key = { it.id }) { transaccion ->
                ListItem(
                    headlineContent = { Text(transaccion.descripcion) },
                    supportingContent = { Text("${transaccion.tipo} — ${transaccion.monto} — ${transaccion.fecha}") },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var descripcion by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }
        var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
        var tipo by remember { mutableStateOf(TipoTransaccion.GASTO) }
        var categoria by remember { mutableStateOf<Categoria?>(null) }
        var negocio by remember { mutableStateOf<Negocio?>(null) }
        var proyecto by remember { mutableStateOf<Proyecto?>(null) }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva transacción") },
            text = {
                Column {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                    )
                    OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto") })
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha (AAAA-MM-DD)") },
                    )
                    SimpleDropdown(
                        label = "Tipo",
                        opciones = TipoTransaccion.entries,
                        seleccionado = tipo,
                        etiqueta = { it.name },
                        onSeleccionar = { tipo = it ?: TipoTransaccion.GASTO },
                    )
                    SimpleDropdown(
                        label = "Categoría",
                        opciones = categorias,
                        seleccionado = categoria,
                        etiqueta = { it.nombre },
                        onSeleccionar = { categoria = it },
                    )
                    SimpleDropdown(
                        label = "Negocio",
                        opciones = negocios,
                        seleccionado = negocio,
                        etiqueta = { it.nombre },
                        onSeleccionar = { negocio = it },
                        permiteNinguno = true,
                    )
                    SimpleDropdown(
                        label = "Proyecto",
                        opciones = proyectos,
                        seleccionado = proyecto,
                        etiqueta = { it.nombre },
                        onSeleccionar = { proyecto = it },
                        permiteNinguno = true,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
                    val fechaLd = runCatching { LocalDate.parse(fecha) }.getOrNull()
                    val categoriaSeleccionada = categoria
                    if (descripcion.isNotBlank() && montoBd != null && fechaLd != null && categoriaSeleccionada != null) {
                        viewModel.crear(
                            descripcion = descripcion,
                            monto = montoBd,
                            fecha = fechaLd,
                            tipo = tipo,
                            categoriaId = categoriaSeleccionada.id,
                            negocioId = negocio?.id,
                            proyectoId = proyecto?.id,
                        )
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
private fun NegociosTab(
    onNegocioClick: (Long) -> Unit,
    viewModel: NegociosViewModel = hiltViewModel(),
) {
    val negocios by viewModel.negocios.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(negocios, key = { it.id }) { negocio ->
                ListItem(
                    modifier = Modifier.clickable { onNegocioClick(negocio.id) },
                    headlineContent = { Text(negocio.nombre) },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo negocio") },
            text = {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nombre.isNotBlank()) {
                        viewModel.crear(nombre)
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
private fun ProyectosTab(
    onProyectoClick: (Long) -> Unit,
    viewModel: ProyectosViewModel = hiltViewModel(),
) {
    val proyectos by viewModel.proyectos.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(proyectos, key = { it.proyecto.id }) { item ->
                ListItem(
                    modifier = Modifier.clickable { onProyectoClick(item.proyecto.id) },
                    headlineContent = { Text(item.proyecto.nombre) },
                    supportingContent = {
                        Text("Gastado: ${item.costoAcumulado} — Restante: ${item.presupuestoRestante}")
                    },
                )
            }
        }
    }

    if (mostrarDialogo) {
        var nombre by remember { mutableStateOf("") }
        var presupuesto by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo proyecto") },
            text = {
                Column {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(
                        value = presupuesto,
                        onValueChange = { presupuesto = it },
                        label = { Text("Presupuesto estimado") },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val presupuestoBd = runCatching { BigDecimal(presupuesto) }.getOrNull()
                    if (nombre.isNotBlank() && presupuestoBd != null) {
                        viewModel.crear(nombre, presupuestoBd)
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

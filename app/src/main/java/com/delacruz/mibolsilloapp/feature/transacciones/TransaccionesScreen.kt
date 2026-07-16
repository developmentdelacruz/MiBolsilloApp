@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
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
import com.delacruz.mibolsilloapp.core.ui.components.DonutChart
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FechaCampo
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.IconoCategoria
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.TipoCuenta
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun ResumenScreen(viewModel: TransaccionesViewModel = hiltViewModel()) {
    val transacciones by viewModel.transacciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val balanceTotal by viewModel.balanceTotal.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()

    val hoy = remember { LocalDate.now() }
    val transaccionesDelMes = remember(transacciones, hoy) {
        transacciones.filter { it.fecha.year == hoy.year && it.fecha.month == hoy.month }
    }
    val ingresosDelMes = remember(transaccionesDelMes) {
        transaccionesDelMes
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .fold(BigDecimal.ZERO) { acumulado, t -> acumulado + t.monto }
    }
    val gastosDelMes = remember(transaccionesDelMes) {
        transaccionesDelMes
            .filter { it.tipo == TipoTransaccion.GASTO }
            .fold(BigDecimal.ZERO) { acumulado, t -> acumulado + t.monto }
    }
    val gastoPorCategoria = remember(transaccionesDelMes, categorias) {
        transaccionesDelMes
            .filter { it.tipo == TipoTransaccion.GASTO }
            .groupBy { it.categoriaId }
            .map { (categoriaId, lista) ->
                val cat = categorias.find { it.id == categoriaId }
                Triple(
                    cat?.nombre ?: "Sin categoría",
                    cat?.icono ?: "💰",
                    lista.fold(BigDecimal.ZERO) { acumulado, t -> acumulado + t.monto },
                )
            }
            .sortedByDescending { it.third }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen") }) },
    ) { padding ->
        if (transacciones.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Insights,
                mensaje = "Todavía no hay datos para mostrar un resumen.",
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Balance total", style = MaterialTheme.typography.labelMedium)
                        MontoTexto(
                            texto = balanceTotal.formatearMonto(simbolo),
                            esPositivo = balanceTotal.signum() >= 0,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ingresos del mes", style = MaterialTheme.typography.labelMedium)
                            MontoTexto(texto = ingresosDelMes.formatearMonto(simbolo), esPositivo = true)
                        }
                    }
                    Card(modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Gastos del mes", style = MaterialTheme.typography.labelMedium)
                            MontoTexto(texto = gastosDelMes.formatearMonto(simbolo), esPositivo = false)
                        }
                    }
                }
            }
            if (gastoPorCategoria.isNotEmpty()) {
                item {
                    Column {
                        Text("Gasto por categoría este mes", style = MaterialTheme.typography.titleMedium)
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            DonutChart(valores = gastoPorCategoria.map { it.third.toFloat() })
                        }
                    }
                }
                items(gastoPorCategoria) { (nombre, icono, monto) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        IconoCategoria(icono = icono)
                        Text(nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(monto.formatearMonto(simbolo), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun TransaccionesScreen(viewModel: TransaccionesViewModel = hiltViewModel()) {
    val transacciones by viewModel.transacciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val negocios by viewModel.negocios.collectAsState()
    val proyectos by viewModel.proyectos.collectAsState()
    val cuentas by viewModel.cuentas.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    var textoBusqueda by remember { mutableStateOf("") }
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtroCategoria by remember { mutableStateOf<Categoria?>(null) }
    var filtroNegocio by remember { mutableStateOf<Negocio?>(null) }
    var filtroProyecto by remember { mutableStateOf<Proyecto?>(null) }

    val transaccionesFiltradas = remember(transacciones, textoBusqueda, filtroCategoria, filtroNegocio, filtroProyecto) {
        transacciones.filter { t ->
            (textoBusqueda.isBlank() || t.descripcion.contains(textoBusqueda, ignoreCase = true)) &&
                (filtroCategoria == null || t.categoriaId == filtroCategoria?.id) &&
                (filtroNegocio == null || t.negocioId == filtroNegocio?.id) &&
                (filtroProyecto == null || t.proyectoId == filtroProyecto?.id)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transacciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar transacción")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = { Text("Buscar") },
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filtros")
                }
            }
            if (mostrarFiltros) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    SimpleDropdown(
                        label = "Categoría",
                        opciones = categorias,
                        seleccionado = filtroCategoria,
                        etiqueta = { it.nombre },
                        onSeleccionar = { filtroCategoria = it },
                        permiteNinguno = true,
                    )
                    SimpleDropdown(
                        label = "Negocio",
                        opciones = negocios,
                        seleccionado = filtroNegocio,
                        etiqueta = { it.nombre },
                        onSeleccionar = { filtroNegocio = it },
                        permiteNinguno = true,
                    )
                    SimpleDropdown(
                        label = "Proyecto",
                        opciones = proyectos,
                        seleccionado = filtroProyecto,
                        etiqueta = { it.nombre },
                        onSeleccionar = { filtroProyecto = it },
                        permiteNinguno = true,
                    )
                }
            }
            if (transaccionesFiltradas.isEmpty()) {
                EstadoVacio(
                    icono = Icons.Filled.Receipt,
                    mensaje = if (transacciones.isEmpty()) {
                        "Todavía no registraste ninguna transacción."
                    } else {
                        "Ninguna transacción coincide con la búsqueda."
                    },
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(transaccionesFiltradas, key = { it.id }) { transaccion ->
                        val categoriaIcono = categorias.find { it.id == transaccion.categoriaId }?.icono ?: "💰"
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.eliminar(transaccion)
                                    true
                                } else {
                                    false
                                }
                            },
                        )
                        SwipeToDismissBox(
                            state = dismissState,
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
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            ) {
                                ListItem(
                                    leadingContent = { IconoCategoria(icono = categoriaIcono) },
                                    headlineContent = { Text(transaccion.descripcion) },
                                    supportingContent = { Text("${transaccion.fecha}") },
                                    trailingContent = {
                                        MontoTexto(
                                            texto = transaccion.monto.formatearMonto(simbolo),
                                            esPositivo = transaccion.tipo == TipoTransaccion.INGRESO,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        var descripcion by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }
        var fecha by remember { mutableStateOf(LocalDate.now()) }
        var tipo by remember { mutableStateOf(TipoTransaccion.GASTO) }
        var categoria by remember { mutableStateOf<Categoria?>(null) }
        var cuenta by remember { mutableStateOf<Cuenta?>(null) }
        var negocio by remember { mutableStateOf<Negocio?>(null) }
        var proyecto by remember { mutableStateOf<Proyecto?>(null) }

        val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
        val puedeGuardar = descripcion.isNotBlank() && montoBd != null && categoria != null && cuenta != null

        FormularioHoja(
            titulo = "Nueva transacción",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val categoriaSeleccionada = categoria
                val cuentaSeleccionada = cuenta
                if (puedeGuardar && categoriaSeleccionada != null && cuentaSeleccionada != null && montoBd != null) {
                    viewModel.crear(
                        descripcion = descripcion,
                        monto = montoBd,
                        fecha = fecha,
                        tipo = tipo,
                        categoriaId = categoriaSeleccionada.id,
                        cuentaId = cuentaSeleccionada.id,
                        negocioId = negocio?.id,
                        proyectoId = proyecto?.id,
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
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth(),
            )
            FechaCampo(fecha = fecha, onFechaSeleccionada = { fecha = it }, modifier = Modifier.fillMaxWidth())
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
            SimpleDropdown(
                label = "Proyecto",
                opciones = proyectos,
                seleccionado = proyecto,
                etiqueta = { it.nombre },
                onSeleccionar = { proyecto = it },
                permiteNinguno = true,
            )
        }
    }
}

@Composable
fun CuentasScreen(viewModel: CuentasViewModel = hiltViewModel()) {
    val cuentasConSaldo by viewModel.cuentasConSaldo.collectAsState()
    val monedas by viewModel.monedas.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuentas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar cuenta")
            }
        },
    ) { padding ->
        if (cuentasConSaldo.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.AccountBalance,
                mensaje = "Creá tu primera cuenta antes de registrar transacciones.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(cuentasConSaldo, key = { it.cuenta.id }) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.eliminar(item.cuenta)
                                true
                            } else {
                                false
                            }
                        },
                    )
                    SwipeToDismissBox(
                        state = dismissState,
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            ListItem(
                                headlineContent = { Text(item.cuenta.nombre) },
                                supportingContent = { Text(item.cuenta.tipo.name) },
                                trailingContent = {
                                    MontoTexto(
                                        texto = item.saldoActual.formatearMonto(simbolo),
                                        esPositivo = item.saldoActual.signum() >= 0,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        var nombre by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf(TipoCuenta.EFECTIVO) }
        var moneda by remember { mutableStateOf<Moneda?>(null) }
        var saldoInicial by remember { mutableStateOf("0") }

        val saldoBd = runCatching { BigDecimal(saldoInicial) }.getOrNull()
        val puedeGuardar = nombre.isNotBlank() && saldoBd != null && moneda != null

        FormularioHoja(
            titulo = "Nueva cuenta",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val monedaSeleccionada = moneda
                if (puedeGuardar && monedaSeleccionada != null && saldoBd != null) {
                    viewModel.crear(nombre, tipo, monedaSeleccionada.id, saldoBd)
                    mostrarFormulario = false
                }
            },
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
            SimpleDropdown(
                label = "Tipo",
                opciones = TipoCuenta.entries,
                seleccionado = tipo,
                etiqueta = { it.name },
                onSeleccionar = { tipo = it ?: TipoCuenta.EFECTIVO },
            )
            SimpleDropdown(
                label = "Moneda",
                opciones = monedas,
                seleccionado = moneda,
                etiqueta = { it.nombre },
                onSeleccionar = { moneda = it },
            )
            OutlinedTextField(
                value = saldoInicial,
                onValueChange = { saldoInicial = it },
                label = { Text("Saldo inicial") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun NegociosScreen(
    onNegocioClick: (Long) -> Unit,
    viewModel: NegociosViewModel = hiltViewModel(),
) {
    val negocios by viewModel.negocios.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Negocios") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar negocio")
            }
        },
    ) { padding ->
        if (negocios.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Store,
                mensaje = "Todavía no registraste ningún negocio.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(negocios, key = { it.id }) { negocio ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNegocioClick(negocio.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(headlineContent = { Text(negocio.nombre) })
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        var nombre by remember { mutableStateOf("") }
        FormularioHoja(
            titulo = "Nuevo negocio",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = nombre.isNotBlank(),
            onGuardar = {
                if (nombre.isNotBlank()) {
                    viewModel.crear(nombre)
                    mostrarFormulario = false
                }
            },
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun ProyectosScreen(
    onProyectoClick: (Long) -> Unit,
    viewModel: ProyectosViewModel = hiltViewModel(),
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Proyectos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar proyecto")
            }
        },
    ) { padding ->
        if (proyectos.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Flag,
                mensaje = "Todavía no creaste ningún proyecto.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(proyectos, key = { it.proyecto.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onProyectoClick(item.proyecto.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(
                            headlineContent = { Text(item.proyecto.nombre) },
                            supportingContent = {
                                Text("Gastado: ${item.costoAcumulado.formatearMonto(simbolo)}")
                            },
                            trailingContent = {
                                MontoTexto(
                                    texto = "Restante: ${item.presupuestoRestante.formatearMonto(simbolo)}",
                                    esPositivo = item.presupuestoRestante.signum() >= 0,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        var nombre by remember { mutableStateOf("") }
        var presupuesto by remember { mutableStateOf("") }

        val presupuestoBd = runCatching { BigDecimal(presupuesto) }.getOrNull()
        val puedeGuardar = nombre.isNotBlank() && presupuestoBd != null

        FormularioHoja(
            titulo = "Nuevo proyecto",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                if (puedeGuardar && presupuestoBd != null) {
                    viewModel.crear(nombre, presupuestoBd)
                    mostrarFormulario = false
                }
            },
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = presupuesto,
                onValueChange = { presupuesto = it },
                label = { Text("Presupuesto estimado") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

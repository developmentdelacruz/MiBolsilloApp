@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.delacruz.mibolsilloapp.core.ui.components.FilaConSwipe
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.IconoCategoria
import com.delacruz.mibolsilloapp.core.ui.components.LineChart
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TarjetaMetrica
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloTheme
import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.PresupuestoConConsumo
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.SugerenciaRecurrencia
import com.delacruz.mibolsilloapp.domain.model.TipoCuenta
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.flow.first

@Composable
fun ResumenScreen(
    onPresupuestoClick: (Long) -> Unit,
    viewModel: TransaccionesViewModel = hiltViewModel(),
) {
    val transacciones by viewModel.transacciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val negocios by viewModel.negocios.collectAsState()
    val balanceTotal by viewModel.balanceTotal.collectAsState()
    val disponibleParaGastar by viewModel.disponibleParaGastar.collectAsState()
    val patrimonioNeto by viewModel.patrimonioNeto.collectAsState()
    val historialPatrimonio by viewModel.historialPatrimonio.collectAsState()
    val sugerenciasRecurrencia by viewModel.sugerenciasRecurrencia.collectAsState()
    val presupuestosActivos by viewModel.presupuestosActivos.collectAsState()
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
            itemsIndexed(
                sugerenciasRecurrencia,
                key = { _, s -> "${s.negocioId}-${s.categoriaId}" },
            ) { index, sugerencia ->
                SugerenciaRecurrenciaCard(
                    sugerencia = sugerencia,
                    nombreNegocio = negocios.firstOrNull { it.id == sugerencia.negocioId }?.nombre
                        ?: "Este negocio",
                    simbolo = simbolo,
                    onConvertirEnSuscripcion = { viewModel.convertirEnSuscripcion(sugerencia) },
                    onDescartar = { viewModel.descartarSugerencia(sugerencia) },
                    modifier = Modifier.entradaEscalonada(index),
                )
            }
            item {
                TarjetaMetrica(
                    titulo = "Disponible para gastar",
                    valor = disponibleParaGastar.formatearMonto(simbolo),
                    esPositivo = disponibleParaGastar.signum() >= 0,
                    subtitulo = "Balance menos compromisos pendientes y presupuesto del mes",
                    esHero = true,
                    colorContenedor = MaterialTheme.colorScheme.secondaryContainer,
                    colorContenido = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            item {
                TarjetaMetrica(
                    titulo = "Balance total",
                    valor = balanceTotal.formatearMonto(simbolo),
                    esPositivo = balanceTotal.signum() >= 0,
                )
            }
            item {
                TarjetaMetrica(
                    titulo = "Patrimonio neto",
                    valor = patrimonioNeto.formatearMonto(simbolo),
                    esPositivo = patrimonioNeto.signum() >= 0,
                    subtitulo = "Activos menos deuda de tarjetas y compromisos pendientes",
                    contenidoExtra = if (historialPatrimonio.size >= 2) {
                        {
                            LineChart(
                                valores = historialPatrimonio.map { it.valor.toFloat() },
                                formatoValor = { v -> BigDecimal(v.toString()).formatearMonto(simbolo) },
                            )
                        }
                    } else {
                        null
                    },
                )
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
            if (presupuestosActivos.isNotEmpty()) {
                item {
                    Text("Presupuestos", style = MaterialTheme.typography.titleMedium)
                }
                itemsIndexed(
                    presupuestosActivos,
                    key = { _, item -> item.presupuesto.id },
                ) { index, item ->
                    val excedido = item.porcentajeConsumido >= 1f
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .entradaEscalonada(index)
                            .clickable { onPresupuestoClick(item.presupuesto.categoriaId) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            IconoCategoria(icono = item.categoria.icono)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.categoria.nombre, style = MaterialTheme.typography.bodyMedium)
                                val progresoAnimado by animateFloatAsState(
                                    targetValue = item.porcentajeConsumido,
                                    label = "progresoPresupuestoResumen",
                                )
                                LinearProgressIndicator(
                                    progress = { progresoAnimado },
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                    color = if (excedido) {
                                        MiBolsilloTheme.extendedColors.negative
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                )
                            }
                            Text(
                                "${item.consumido.formatearMonto(simbolo)} / " +
                                    item.presupuesto.montoMensual.formatearMonto(simbolo),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (excedido) {
                                    MiBolsilloTheme.extendedColors.negative
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
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
                            DonutChart(
                                valores = gastoPorCategoria.map { it.third.toFloat() },
                                etiquetas = gastoPorCategoria.map { it.first },
                            )
                        }
                    }
                }
                itemsIndexed(gastoPorCategoria) { index, (nombre, icono, monto) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .entradaEscalonada(index),
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
private fun SugerenciaRecurrenciaCard(
    sugerencia: SugerenciaRecurrencia,
    nombreNegocio: String,
    simbolo: String,
    onConvertirEnSuscripcion: () -> Unit,
    onDescartar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pago recurrente detectado",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                "$nombreNegocio: ${sugerencia.montoPromedio.formatearMonto(simbolo)} aprox., " +
                    "${sugerencia.ocurrencias} veces registrado cada mes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onConvertirEnSuscripcion) {
                    Text("Convertir en suscripción")
                }
                TextButton(onClick = onDescartar) {
                    Text("Descartar")
                }
            }
        }
    }
}

private data class ParticipanteForm(val nombreContacto: String, val telefono: String = "", val monto: String = "")

@Composable
fun TransaccionesScreen(viewModel: TransaccionesViewModel = hiltViewModel()) {
    val transacciones by viewModel.transacciones.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val negocios by viewModel.negocios.collectAsState()
    val proyectos by viewModel.proyectos.collectAsState()
    val cuentas by viewModel.cuentas.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Transaccion?>(null) }

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
                    itemsIndexed(transaccionesFiltradas, key = { _, t -> t.id }) { index, transaccion ->
                        val categoriaIcono = categorias.find { it.id == transaccion.categoriaId }?.icono ?: "💰"
                        FilaConSwipe(
                            onEliminar = { viewModel.eliminar(transaccion) },
                            modifier = Modifier.entradaEscalonada(index),
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { elementoEnEdicion = transaccion },
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

    if (mostrarFormulario || elementoEnEdicion != null) {
        var descripcion by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.descripcion ?: "") }
        var monto by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.monto?.toPlainString() ?: "")
        }
        var fecha by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.fecha ?: LocalDate.now()) }
        var tipo by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.tipo ?: TipoTransaccion.GASTO)
        }
        var categoria by remember(elementoEnEdicion) {
            mutableStateOf(categorias.find { it.id == elementoEnEdicion?.categoriaId })
        }
        var cuenta by remember(elementoEnEdicion) {
            mutableStateOf(cuentas.find { it.id == elementoEnEdicion?.cuentaId })
        }
        var negocio by remember(elementoEnEdicion) {
            mutableStateOf(negocios.find { it.id == elementoEnEdicion?.negocioId })
        }
        var proyecto by remember(elementoEnEdicion) {
            mutableStateOf(proyectos.find { it.id == elementoEnEdicion?.proyectoId })
        }
        var dividirConAlguien by remember(elementoEnEdicion) { mutableStateOf(false) }
        var participantes by remember(elementoEnEdicion) { mutableStateOf<List<ParticipanteForm>>(emptyList()) }

        // Los participantes viven en otra tabla (gastos_compartidos), no en Transaccion, así que
        // hay que traerlos aparte cuando se abre una edición — no están disponibles de entrada
        // como el resto de los campos, que salen directo de elementoEnEdicion.
        LaunchedEffect(elementoEnEdicion) {
            val edicion = elementoEnEdicion
            if (edicion != null) {
                val existentes = viewModel.participantesDe(edicion.id).first()
                if (existentes.isNotEmpty()) {
                    dividirConAlguien = true
                    participantes = existentes.map {
                        ParticipanteForm(it.nombreContacto, it.telefono, it.montoAPagar.toPlainString())
                    }
                }
            }
        }

        val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
        val puedeGuardar = descripcion.isNotBlank() && montoBd != null && categoria != null && cuenta != null

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar transacción" else "Nueva transacción",
            onCerrar = { mostrarFormulario = false; elementoEnEdicion = null },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val categoriaSeleccionada = categoria
                val cuentaSeleccionada = cuenta
                if (puedeGuardar && categoriaSeleccionada != null && cuentaSeleccionada != null && montoBd != null) {
                    val participantesValidos = if (dividirConAlguien) {
                        participantes.mapNotNull { p ->
                            val montoParticipante = runCatching { BigDecimal(p.monto) }.getOrNull()
                            if (p.nombreContacto.isNotBlank() && montoParticipante != null) {
                                Triple(p.nombreContacto, p.telefono, montoParticipante)
                            } else {
                                null
                            }
                        }
                    } else {
                        emptyList()
                    }

                    val transaccionExistente = elementoEnEdicion
                    if (transaccionExistente != null) {
                        viewModel.actualizar(
                            transaccionExistente.copy(
                                descripcion = descripcion,
                                monto = montoBd,
                                fecha = fecha,
                                tipo = tipo,
                                categoriaId = categoriaSeleccionada.id,
                                cuentaId = cuentaSeleccionada.id,
                                negocioId = negocio?.id,
                                proyectoId = proyecto?.id,
                            ),
                            participantesValidos,
                        )
                    } else {
                        viewModel.crear(
                            descripcion = descripcion,
                            monto = montoBd,
                            fecha = fecha,
                            tipo = tipo,
                            categoriaId = categoriaSeleccionada.id,
                            cuentaId = cuentaSeleccionada.id,
                            negocioId = negocio?.id,
                            proyectoId = proyecto?.id,
                            participantes = participantesValidos,
                        )
                    }
                    mostrarFormulario = false
                    elementoEnEdicion = null
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
            if (tipo == TipoTransaccion.GASTO) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = dividirConAlguien, onCheckedChange = { dividirConAlguien = it })
                    Text("Dividir con alguien")
                }
                if (dividirConAlguien) {
                    participantes.forEachIndexed { index, participante ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = participante.nombreContacto,
                                onValueChange = { valor ->
                                    participantes = participantes.toMutableList()
                                        .also { it[index] = participante.copy(nombreContacto = valor) }
                                },
                                label = { Text("Nombre") },
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = participante.monto,
                                onValueChange = { valor ->
                                    participantes = participantes.toMutableList()
                                        .also { it[index] = participante.copy(monto = valor) }
                                },
                                label = { Text("Monto") },
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = {
                                participantes = participantes.toMutableList().also { it.removeAt(index) }
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Quitar persona")
                            }
                        }
                    }
                    TextButton(onClick = { participantes = participantes + ParticipanteForm("") }) {
                        Text("Agregar persona")
                    }
                }
            }
        }
    }
}

@Composable
fun CuentasScreen(viewModel: CuentasViewModel = hiltViewModel()) {
    val cuentasConSaldo by viewModel.cuentasConSaldo.collectAsState()
    val monedas by viewModel.monedas.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var elementoEnEdicion by remember { mutableStateOf<Cuenta?>(null) }

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
            val balanceTotalCuentas = remember(cuentasConSaldo) {
                cuentasConSaldo.fold(BigDecimal.ZERO) { acumulado, item -> acumulado + item.saldoActual }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    TarjetaMetrica(
                        titulo = "Balance total de todas las cuentas",
                        valor = balanceTotalCuentas.formatearMonto(simbolo),
                        esPositivo = balanceTotalCuentas.signum() >= 0,
                        esHero = true,
                    )
                }
                itemsIndexed(cuentasConSaldo, key = { _, item -> item.cuenta.id }) { index, item ->
                    FilaConSwipe(
                        onEliminar = { viewModel.eliminar(item.cuenta) },
                        modifier = Modifier.entradaEscalonada(index),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { elementoEnEdicion = item.cuenta },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column {
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
                                val porcentaje = item.porcentajeUtilizado
                                if (porcentaje != null) {
                                    val porcentajeAnimado by animateFloatAsState(
                                        targetValue = porcentaje,
                                        label = "utilizacionTarjeta",
                                    )
                                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                        LinearProgressIndicator(
                                            progress = { porcentajeAnimado },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = if (porcentaje >= 1f) {
                                                MaterialTheme.colorScheme.error
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            },
                                        )
                                        Text(
                                            "Usaste ${(porcentaje * 100).toInt()}% de tu límite",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    }

    if (mostrarFormulario || elementoEnEdicion != null) {
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        var tipo by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.tipo ?: TipoCuenta.EFECTIVO) }
        var moneda by remember(elementoEnEdicion) {
            mutableStateOf(monedas.find { it.id == elementoEnEdicion?.monedaId })
        }
        var saldoInicial by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.saldoInicial?.toPlainString() ?: "0")
        }
        var limiteCredito by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.limiteCredito?.toPlainString() ?: "")
        }

        val saldoBd = runCatching { BigDecimal(saldoInicial) }.getOrNull()
        val puedeGuardar = nombre.isNotBlank() && saldoBd != null && moneda != null

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar cuenta" else "Nueva cuenta",
            onCerrar = { mostrarFormulario = false; elementoEnEdicion = null },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                val monedaSeleccionada = moneda
                if (puedeGuardar && monedaSeleccionada != null && saldoBd != null) {
                    val cuentaExistente = elementoEnEdicion
                    if (cuentaExistente != null) {
                        viewModel.actualizar(
                            cuentaExistente.copy(
                                nombre = nombre,
                                tipo = tipo,
                                monedaId = monedaSeleccionada.id,
                                saldoInicial = saldoBd,
                                limiteCredito = limiteCredito.toBigDecimalOrNull(),
                            ),
                        )
                    } else {
                        viewModel.crear(
                            nombre = nombre,
                            tipo = tipo,
                            monedaId = monedaSeleccionada.id,
                            saldoInicial = saldoBd,
                            limiteCredito = limiteCredito.toBigDecimalOrNull(),
                        )
                    }
                    mostrarFormulario = false
                    elementoEnEdicion = null
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
            if (tipo == TipoCuenta.TARJETA) {
                OutlinedTextField(
                    value = limiteCredito,
                    onValueChange = { limiteCredito = it },
                    label = { Text("Límite de crédito (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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
    var elementoEnEdicion by remember { mutableStateOf<Negocio?>(null) }

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
                itemsIndexed(negocios, key = { _, n -> n.id }) { index, negocio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNegocioClick(negocio.id) }
                            .entradaEscalonada(index),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(
                            headlineContent = { Text(negocio.nombre) },
                            trailingContent = {
                                IconButton(onClick = { elementoEnEdicion = negocio }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar negocio")
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormulario || elementoEnEdicion != null) {
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar negocio" else "Nuevo negocio",
            onCerrar = { mostrarFormulario = false; elementoEnEdicion = null },
            guardarHabilitado = nombre.isNotBlank(),
            onGuardar = {
                if (nombre.isNotBlank()) {
                    val negocioExistente = elementoEnEdicion
                    if (negocioExistente != null) {
                        viewModel.actualizar(negocioExistente.copy(nombre = nombre))
                    } else {
                        viewModel.crear(nombre)
                    }
                    mostrarFormulario = false
                    elementoEnEdicion = null
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
    var elementoEnEdicion by remember { mutableStateOf<Proyecto?>(null) }

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
                itemsIndexed(proyectos, key = { _, item -> item.proyecto.id }) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProyectoClick(item.proyecto.id) }
                            .entradaEscalonada(index),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(
                            headlineContent = { Text(item.proyecto.nombre) },
                            supportingContent = {
                                Text("Gastado: ${item.costoAcumulado.formatearMonto(simbolo)}")
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    MontoTexto(
                                        texto = "Restante: ${item.presupuestoRestante.formatearMonto(simbolo)}",
                                        esPositivo = item.presupuestoRestante.signum() >= 0,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    IconButton(onClick = { elementoEnEdicion = item.proyecto }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar proyecto")
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormulario || elementoEnEdicion != null) {
        var nombre by remember(elementoEnEdicion) { mutableStateOf(elementoEnEdicion?.nombre ?: "") }
        var presupuesto by remember(elementoEnEdicion) {
            mutableStateOf(elementoEnEdicion?.presupuestoEstimado?.toPlainString() ?: "")
        }

        val presupuestoBd = runCatching { BigDecimal(presupuesto) }.getOrNull()
        val puedeGuardar = nombre.isNotBlank() && presupuestoBd != null

        FormularioHoja(
            titulo = if (elementoEnEdicion != null) "Editar proyecto" else "Nuevo proyecto",
            onCerrar = { mostrarFormulario = false; elementoEnEdicion = null },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                if (puedeGuardar && presupuestoBd != null) {
                    val proyectoExistente = elementoEnEdicion
                    if (proyectoExistente != null) {
                        viewModel.actualizar(proyectoExistente.copy(nombre = nombre, presupuestoEstimado = presupuestoBd))
                    } else {
                        viewModel.crear(nombre, presupuestoBd)
                    }
                    mostrarFormulario = false
                    elementoEnEdicion = null
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

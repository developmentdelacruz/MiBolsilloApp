@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.suscripciones

import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.ChipEstado
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.FormularioHoja
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.core.ui.components.TonoChip
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import java.math.BigDecimal

@Composable
fun SuscripcionDetalleScreen(viewModel: SuscripcionDetalleViewModel = hiltViewModel()) {
    val detalle by viewModel.detalle.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()
    val context = LocalContext.current
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(detalle?.suscripcion?.nombre ?: "Suscripción") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar invitado")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            detalle?.let { info ->
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Día de cobro: ${info.suscripcion.diaCobro}", style = MaterialTheme.typography.bodyMedium)
                        MontoTexto(
                            texto = "${info.suscripcion.montoMensual.formatearMonto(simbolo)}/mes",
                            esPositivo = false,
                        )
                    }
                }
            }

            val invitados = detalle?.invitados.orEmpty()
            if (invitados.isEmpty()) {
                EstadoVacio(icono = Icons.Filled.People, mensaje = "Todavía no agregaste invitados")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(invitados, key = { it.id }) { invitado ->
                        val suscripcionNombre = detalle?.suscripcion?.nombre.orEmpty()
                        val estadoDismiss = rememberSwipeToDismissBoxState(
                            confirmValueChange = { valor ->
                                if (valor == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.eliminarInvitado(invitado)
                                    true
                                } else {
                                    false
                                }
                            },
                        )
                        SwipeToDismissBox(
                            state = estadoDismiss,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.errorContainer),
                                    contentAlignment = Alignment.CenterEnd,
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                    )
                                }
                            },
                        ) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(invitado.nombreContacto, style = MaterialTheme.typography.titleMedium)
                                        ChipEstado(
                                            texto = invitado.estadoPago.name,
                                            tono = if (invitado.estadoPago == EstadoPago.PAGADO) {
                                                TonoChip.POSITIVO
                                            } else {
                                                TonoChip.NEGATIVO
                                            },
                                        )
                                    }
                                    MontoTexto(
                                        texto = invitado.montoAPagar.formatearMonto(simbolo),
                                        esPositivo = invitado.estadoPago == EstadoPago.PAGADO,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                        TextButton(onClick = {
                                            val texto = "Hola ${invitado.nombreContacto}, te recuerdo tu parte de " +
                                                "$suscripcionNombre: ${invitado.montoAPagar.formatearMonto(simbolo)}. ¡Gracias!"
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_TEXT, texto)
                                            }
                                            context.startActivity(Intent.createChooser(intent, null))
                                        }) { Text("Compartir") }
                                        if (invitado.estadoPago == EstadoPago.PENDIENTE) {
                                            TextButton(onClick = { viewModel.marcarPagado(invitado) }) {
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
    }

    if (mostrarFormulario) {
        var nombreContacto by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }

        val montoBd = monto.toBigDecimalOrNull()
        val puedeGuardar = nombreContacto.isNotBlank() && montoBd != null

        FormularioHoja(
            titulo = "Agregar invitado",
            onCerrar = { mostrarFormulario = false },
            guardarHabilitado = puedeGuardar,
            onGuardar = {
                viewModel.agregarInvitado(nombreContacto, telefono, montoBd!!)
                mostrarFormulario = false
            },
        ) {
            OutlinedTextField(
                value = nombreContacto,
                onValueChange = { nombreContacto = it },
                label = { Text("Nombre del contacto") },
            )
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
            OutlinedTextField(value = monto, onValueChange = { monto = it }, label = { Text("Monto a pagar") })
        }
    }
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}

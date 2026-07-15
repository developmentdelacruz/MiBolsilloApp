@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.suscripciones

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.domain.model.EstadoPago
import java.math.BigDecimal

@Composable
fun SuscripcionDetalleScreen(viewModel: SuscripcionDetalleViewModel = hiltViewModel()) {
    val detalle by viewModel.detalle.collectAsState()
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(detalle?.suscripcion?.nombre ?: "Suscripción") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) { Text("+") }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            detalle?.let { info ->
                Text("Monto mensual: ${info.suscripcion.montoMensual} — día ${info.suscripcion.diaCobro}")
            }
            LazyColumn {
                items(detalle?.invitados.orEmpty(), key = { it.id }) { invitado ->
                    val suscripcionNombre = detalle?.suscripcion?.nombre.orEmpty()
                    ListItem(
                        headlineContent = { Text(invitado.nombreContacto) },
                        supportingContent = { Text("${invitado.montoAPagar} — ${invitado.estadoPago}") },
                        trailingContent = {
                            Row {
                                TextButton(onClick = {
                                    val texto = "Hola ${invitado.nombreContacto}, te recuerdo tu parte de " +
                                        "$suscripcionNombre: ${invitado.montoAPagar}. ¡Gracias!"
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
                        },
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        var nombreContacto by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var monto by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Agregar invitado") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nombreContacto,
                        onValueChange = { nombreContacto = it },
                        label = { Text("Nombre del contacto") },
                    )
                    OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto a pagar") },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val montoBd = runCatching { BigDecimal(monto) }.getOrNull()
                    if (nombreContacto.isNotBlank() && montoBd != null) {
                        viewModel.agregarInvitado(nombreContacto, telefono, montoBd)
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

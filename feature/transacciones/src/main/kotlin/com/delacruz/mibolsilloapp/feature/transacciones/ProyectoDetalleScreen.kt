@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProyectoDetalleScreen(viewModel: ProyectoDetalleViewModel = hiltViewModel()) {
    val proyecto by viewModel.proyecto.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(proyecto?.proyecto?.nombre ?: "Proyecto") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            proyecto?.let { info ->
                Text("Presupuesto: ${info.proyecto.presupuestoEstimado}")
                Text("Gastado: ${info.costoAcumulado} — Restante: ${info.presupuestoRestante}")
            }
            LazyColumn {
                items(transacciones, key = { it.id }) { transaccion ->
                    ListItem(
                        headlineContent = { Text(transaccion.descripcion) },
                        supportingContent = { Text("${transaccion.tipo} — ${transaccion.monto} — ${transaccion.fecha}") },
                    )
                }
            }
        }
    }
}

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

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
fun NegocioDetalleScreen(viewModel: NegocioDetalleViewModel = hiltViewModel()) {
    val negocio by viewModel.negocio.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(negocio?.nombre ?: "Negocio") }) },
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
}

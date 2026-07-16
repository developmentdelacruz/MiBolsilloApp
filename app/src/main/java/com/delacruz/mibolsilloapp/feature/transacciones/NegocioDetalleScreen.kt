@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.delacruz.mibolsilloapp.core.ui.components.EstadoVacio
import com.delacruz.mibolsilloapp.core.ui.components.MontoTexto
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion

@Composable
fun NegocioDetalleScreen(viewModel: NegocioDetalleViewModel = hiltViewModel()) {
    val negocio by viewModel.negocio.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(negocio?.nombre ?: "Negocio") }) },
    ) { padding ->
        if (transacciones.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.Receipt,
                mensaje = "Este negocio todavía no tiene transacciones.",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(transacciones, key = { it.id }) { transaccion ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        ListItem(
                            headlineContent = { Text(transaccion.descripcion) },
                            supportingContent = { Text("${transaccion.fecha}") },
                            trailingContent = {
                                MontoTexto(
                                    texto = transaccion.monto.toString(),
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

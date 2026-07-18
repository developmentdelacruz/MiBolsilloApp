@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
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
import com.delacruz.mibolsilloapp.core.ui.components.FilaTransaccion
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.core.ui.components.formatearMonto
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion

@Composable
fun NegocioDetalleScreen(viewModel: NegocioDetalleViewModel = hiltViewModel()) {
    val negocio by viewModel.negocio.collectAsState()
    val transacciones by viewModel.transacciones.collectAsState()
    val simbolo by viewModel.simboloMoneda.collectAsState()

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
                itemsIndexed(transacciones, key = { _, t -> t.id }) { index, transaccion ->
                    FilaTransaccion(
                        descripcion = transaccion.descripcion,
                        fecha = "${transaccion.fecha}",
                        montoFormateado = transaccion.monto.formatearMonto(simbolo),
                        esPositivo = transaccion.tipo == TipoTransaccion.INGRESO,
                        modifier = Modifier.entradaEscalonada(index),
                    )
                }
            }
        }
    }
}

@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.respaldo

import android.net.Uri
import android.os.Process
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.delay

@Composable
fun RespaldoScreen(viewModel: RespaldoViewModel = hiltViewModel()) {
    val estado by viewModel.estado.collectAsState()
    var origenPendiente by remember { mutableStateOf<Uri?>(null) }

    val lanzadorExportar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/x-sqlite3"),
        onResult = { destino -> destino?.let { viewModel.exportar(it) } },
    )
    val lanzadorRestaurar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { origen -> origen?.let { origenPendiente = it } },
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Respaldo") }) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Un respaldo es una copia completa de tus datos (cuentas, transacciones, " +
                    "presupuestos, compromisos, suscripciones). Exportalo para guardarlo en Drive, " +
                    "tu almacenamiento o compartirlo. Restaurar reemplaza TODOS tus datos actuales " +
                    "por los del archivo que elijas.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = { lanzadorExportar.launch("finanzas360_respaldo_${LocalDate.now()}.db") },
                modifier = Modifier.fillMaxWidth(),
                enabled = estado !is EstadoRespaldo.Procesando,
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Exportar respaldo")
            }

            OutlinedButton(
                onClick = { lanzadorRestaurar.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth(),
                enabled = estado !is EstadoRespaldo.Procesando,
            ) {
                Text("Restaurar respaldo")
            }

            when (val estadoActual = estado) {
                is EstadoRespaldo.Procesando -> CircularProgressIndicator()
                is EstadoRespaldo.ExportacionExitosa -> Text(
                    "Respaldo exportado correctamente.",
                    color = MaterialTheme.colorScheme.primary,
                )
                is EstadoRespaldo.Error -> Text(
                    estadoActual.mensaje,
                    color = MaterialTheme.colorScheme.error,
                )
                is EstadoRespaldo.RestauracionExitosa -> Text(
                    "Datos restaurados. La app se reiniciará...",
                    color = MaterialTheme.colorScheme.primary,
                )
                is EstadoRespaldo.Inactivo -> Unit
            }
        }
    }

    val origen = origenPendiente
    if (origen != null) {
        AlertDialog(
            onDismissRequest = { origenPendiente = null },
            title = { Text("¿Restaurar respaldo?") },
            text = { Text("Esto reemplaza todos tus datos actuales por los del respaldo. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.restaurar(origen)
                        origenPendiente = null
                    },
                ) { Text("Restaurar") }
            },
            dismissButton = {
                TextButton(onClick = { origenPendiente = null }) { Text("Cancelar") }
            },
        )
    }

    // Room cachea la conexión en un singleton por el ciclo de vida del proceso — tras
    // sobrescribir el archivo .db por debajo hay que reiniciar el proceso para que la
    // próxima apertura lea la base restaurada en vez de arrastrar estado en memoria.
    if (estado is EstadoRespaldo.RestauracionExitosa) {
        LaunchedEffect(Unit) {
            delay(1500)
            Process.killProcess(Process.myPid())
        }
    }
}

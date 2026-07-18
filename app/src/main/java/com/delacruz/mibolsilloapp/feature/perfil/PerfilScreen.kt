@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.delacruz.mibolsilloapp.feature.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PerfilScreen(viewModel: PerfilViewModel = hiltViewModel()) {
    val nombre by viewModel.nombre.collectAsState()
    val guardado by viewModel.guardado.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil") }) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Con tu nombre, las notificaciones de la app se sienten un poco más tuyas.",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = viewModel::actualizarNombre,
                label = { Text("Tu nombre") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = viewModel::guardar,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Guardar") }
            if (guardado) {
                Text("Guardado.", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

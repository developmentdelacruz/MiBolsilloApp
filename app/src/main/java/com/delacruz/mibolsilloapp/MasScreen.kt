package com.delacruz.mibolsilloapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.delacruz.mibolsilloapp.feature.catalogos.RUTA_CATALOGOS
import com.delacruz.mibolsilloapp.feature.suscripciones.RUTA_SUSCRIPCIONES
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_NEGOCIOS
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_PROYECTOS

const val RUTA_MAS = "mas"

private data class ItemMas(
    val ruta: String,
    val etiqueta: String,
    val descripcion: String,
    val icono: ImageVector,
)

/** Secciones de baja frecuencia: no compiten por espacio con lo que se usa a diario. */
private val itemsMas = listOf(
    ItemMas(
        RUTA_SUSCRIPCIONES,
        "Suscripciones",
        "Pagos recurrentes y miembros compartidos",
        Icons.Filled.Subscriptions,
    ),
    ItemMas(
        RUTA_CATALOGOS,
        "Categorías, monedas y presupuestos",
        "Catálogos y límites de gasto mensual",
        Icons.Filled.Category,
    ),
    ItemMas(RUTA_NEGOCIOS, "Negocios", "Transacciones agrupadas por negocio", Icons.Filled.Store),
    ItemMas(RUTA_PROYECTOS, "Proyectos", "Presupuesto y gasto por proyecto", Icons.Filled.Flag),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Más") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(itemsMas, key = { it.ruta }) { item ->
                Card(
                    modifier = Modifier.clickable { navController.navigate(item.ruta) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    ListItem(
                        headlineContent = { Text(item.etiqueta) },
                        supportingContent = { Text(item.descripcion) },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = item.icono,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    )
                }
            }
        }
    }
}

package com.delacruz.mibolsilloapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.delacruz.mibolsilloapp.core.ui.components.entradaEscalonada
import com.delacruz.mibolsilloapp.feature.catalogos.RUTA_CATALOGOS
import com.delacruz.mibolsilloapp.feature.gastoscompartidos.RUTA_GASTOS_COMPARTIDOS
import com.delacruz.mibolsilloapp.feature.perfil.RUTA_PERFIL
import com.delacruz.mibolsilloapp.feature.respaldo.RUTA_RESPALDO
import com.delacruz.mibolsilloapp.feature.suscripciones.RUTA_SUSCRIPCIONES
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_COMPRAS
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
    ItemMas(RUTA_COMPRAS, "Compras en cuotas", "Compras grandes divididas en pagos mensuales", Icons.Filled.CreditCard),
    ItemMas(RUTA_RESPALDO, "Respaldo", "Exportar o restaurar una copia de tus datos", Icons.Filled.CloudUpload),
    ItemMas(RUTA_PERFIL, "Perfil", "Tu nombre, para personalizar las notificaciones", Icons.Filled.Person),
    ItemMas(
        RUTA_GASTOS_COMPARTIDOS,
        "Gastos compartidos",
        "Quién te debe qué de los gastos que dividiste",
        Icons.Filled.People,
    ),
)

/** Rota entre colores de contenedor del tema según el índice — variedad visual sin salirse del tema. */
@Composable
private fun coloresPorIndice(indice: Int): Pair<Color, Color> {
    val paleta = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer,
    )
    return paleta[indice % paleta.size]
}

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
            itemsIndexed(itemsMas, key = { _, item -> item.ruta }) { index, item ->
                val interactionSource = remember { MutableInteractionSource() }
                val presionado by interactionSource.collectIsPressedAsState()
                val escala by animateFloatAsState(targetValue = if (presionado) 0.96f else 1f, label = "escalaMenu")
                val (colorContenedorIcono, colorIcono) = coloresPorIndice(index)

                Card(
                    modifier = Modifier
                        .entradaEscalonada(index)
                        .graphicsLayer {
                            scaleX = escala
                            scaleY = escala
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current,
                        ) { navController.navigate(item.ruta) },
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
                                    .background(colorContenedorIcono),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = item.icono,
                                    contentDescription = null,
                                    tint = colorIcono,
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

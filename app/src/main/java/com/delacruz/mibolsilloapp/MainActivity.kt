package com.delacruz.mibolsilloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloAppTheme
import com.delacruz.mibolsilloapp.feature.catalogos.RUTA_CATALOGOS
import com.delacruz.mibolsilloapp.feature.catalogos.catalogosGraph
import com.delacruz.mibolsilloapp.feature.compromisos.RUTA_COMPROMISOS
import com.delacruz.mibolsilloapp.feature.compromisos.compromisosGraph
import com.delacruz.mibolsilloapp.feature.suscripciones.RUTA_SUSCRIPCIONES
import com.delacruz.mibolsilloapp.feature.suscripciones.suscripcionesGraph
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_TRANSACCIONES
import com.delacruz.mibolsilloapp.feature.transacciones.transaccionesGraph
import dagger.hilt.android.AndroidEntryPoint

private data class DestinoRaiz(val ruta: String, val etiqueta: String)

private val destinosRaiz = listOf(
    DestinoRaiz(RUTA_TRANSACCIONES, "Flujo"),
    DestinoRaiz(RUTA_COMPROMISOS, "Compromisos"),
    DestinoRaiz(RUTA_SUSCRIPCIONES, "Suscripciones"),
    DestinoRaiz(RUTA_CATALOGOS, "Catálogos"),
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiBolsilloAppTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = RUTA_TRANSACCIONES,
            modifier = Modifier.padding(padding),
        ) {
            transaccionesGraph(navController)
            compromisosGraph(navController)
            suscripcionesGraph(navController)
            catalogosGraph()
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    NavigationBar {
        destinosRaiz.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = {
                    navController.navigate(destino.ruta) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {},
                label = { Text(destino.etiqueta) },
            )
        }
    }
}

package com.delacruz.mibolsilloapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.delacruz.mibolsilloapp.core.ui.theme.MiBolsilloAppTheme
import com.delacruz.mibolsilloapp.feature.catalogos.catalogosGraph
import com.delacruz.mibolsilloapp.feature.compromisos.RUTA_COMPROMISOS
import com.delacruz.mibolsilloapp.feature.compromisos.compromisosGraph
import com.delacruz.mibolsilloapp.feature.suscripciones.suscripcionesGraph
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_CUENTAS
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_RESUMEN
import com.delacruz.mibolsilloapp.feature.transacciones.RUTA_TRANSACCIONES
import com.delacruz.mibolsilloapp.feature.transacciones.cuentasGraph
import com.delacruz.mibolsilloapp.feature.transacciones.negociosGraph
import com.delacruz.mibolsilloapp.feature.transacciones.proyectosGraph
import com.delacruz.mibolsilloapp.feature.transacciones.resumenGraph
import com.delacruz.mibolsilloapp.feature.transacciones.transaccionesGraph
import dagger.hilt.android.AndroidEntryPoint

private data class DestinoRaiz(val ruta: String, val etiqueta: String, val icono: ImageVector)

/**
 * Solo las secciones de uso diario van al bottom nav (máx. 5, guía de Material 3).
 * Suscripciones/Catálogos/Negocios/Proyectos viven detrás de "Más": son de baja
 * frecuencia y no deberían competir por espacio con Resumen/Cuentas/Transacciones.
 */
private val destinosRaiz = listOf(
    DestinoRaiz(RUTA_RESUMEN, "Resumen", Icons.Filled.Insights),
    DestinoRaiz(RUTA_CUENTAS, "Cuentas", Icons.Filled.AccountBalanceWallet),
    DestinoRaiz(RUTA_TRANSACCIONES, "Transacciones", Icons.Filled.Receipt),
    DestinoRaiz(RUTA_COMPROMISOS, "Compromisos", Icons.Filled.Handshake),
    DestinoRaiz(RUTA_MAS, "Más", Icons.Filled.MoreHoriz),
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
    SolicitarPermisoNotificaciones()

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = RUTA_RESUMEN,
            modifier = Modifier.padding(padding),
        ) {
            resumenGraph()
            cuentasGraph()
            transaccionesGraph()
            negociosGraph(navController)
            proyectosGraph(navController)
            compromisosGraph(navController)
            suscripcionesGraph(navController)
            catalogosGraph()
            composable(RUTA_MAS) { MasScreen(navController) }
        }
    }
}

/**
 * En Android 13+ (API 33) mostrar una notificación requiere el permiso POST_NOTIFICATIONS
 * concedido en tiempo de ejecución — sin esto, PaymentReminderWorker corre y calcula todo
 * bien, pero la notificación nunca aparece. En versiones anteriores el permiso no existe
 * (se otorga automáticamente), por eso el chequeo de SDK_INT.
 */
@Composable
private fun SolicitarPermisoNotificaciones() {
    val context = LocalContext.current
    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {},
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                    // Sin saveState/restoreState a propósito: Suscripciones/Catálogos/Negocios/
                    // Proyectos se apilan sobre "mas" con un navigate() simple (no son grafos
                    // anidados por tab), así que restoreState terminaba mostrando esa pantalla
                    // apilada en vez de la base del tab. popUpTo sin guardar estado limpia
                    // siempre hasta el destino real de cada tab.
                    navController.navigate(destino.ruta) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(imageVector = destino.icono, contentDescription = destino.etiqueta) },
                label = { Text(destino.etiqueta) },
            )
        }
    }
}

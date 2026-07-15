package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RUTA_TRANSACCIONES = "transacciones"
private const val RUTA_NEGOCIO_DETALLE = "negocios/{negocioId}"
private const val RUTA_PROYECTO_DETALLE = "proyectos/{proyectoId}"

fun NavGraphBuilder.transaccionesGraph(navController: NavController) {
    composable(RUTA_TRANSACCIONES) {
        TransaccionesScreen(
            onNegocioClick = { id -> navController.navigate("negocios/$id") },
            onProyectoClick = { id -> navController.navigate("proyectos/$id") },
        )
    }
    composable(
        route = RUTA_NEGOCIO_DETALLE,
        arguments = listOf(navArgument("negocioId") { type = NavType.LongType }),
    ) {
        NegocioDetalleScreen()
    }
    composable(
        route = RUTA_PROYECTO_DETALLE,
        arguments = listOf(navArgument("proyectoId") { type = NavType.LongType }),
    ) {
        ProyectoDetalleScreen()
    }
}

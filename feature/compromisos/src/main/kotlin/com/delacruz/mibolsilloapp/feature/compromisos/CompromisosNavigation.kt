package com.delacruz.mibolsilloapp.feature.compromisos

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RUTA_COMPROMISOS = "compromisos"
private const val RUTA_DETALLE = "compromisos/{compromisoId}"

fun NavGraphBuilder.compromisosGraph(navController: NavController) {
    composable(RUTA_COMPROMISOS) {
        CompromisosListScreen(
            onCompromisoClick = { id -> navController.navigate("compromisos/$id") },
        )
    }
    composable(
        route = RUTA_DETALLE,
        arguments = listOf(navArgument("compromisoId") { type = NavType.LongType }),
    ) {
        CompromisoDetalleScreen()
    }
}

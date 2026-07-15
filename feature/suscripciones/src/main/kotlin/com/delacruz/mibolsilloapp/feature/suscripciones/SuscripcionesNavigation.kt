package com.delacruz.mibolsilloapp.feature.suscripciones

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RUTA_SUSCRIPCIONES = "suscripciones"
private const val RUTA_DETALLE = "suscripciones/{suscripcionId}"

fun NavGraphBuilder.suscripcionesGraph(navController: NavController) {
    composable(RUTA_SUSCRIPCIONES) {
        SuscripcionesListScreen(
            onSuscripcionClick = { id -> navController.navigate("suscripciones/$id") },
        )
    }
    composable(
        route = RUTA_DETALLE,
        arguments = listOf(navArgument("suscripcionId") { type = NavType.LongType }),
    ) {
        SuscripcionDetalleScreen()
    }
}

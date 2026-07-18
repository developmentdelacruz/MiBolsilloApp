package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val RUTA_CATALOGOS = "catalogos"
const val RUTA_PRESUPUESTO_DETALLE_BASE = "catalogos/presupuestos"
private const val RUTA_PRESUPUESTO_DETALLE = "$RUTA_PRESUPUESTO_DETALLE_BASE/{categoriaId}"

fun NavGraphBuilder.catalogosGraph(navController: NavController) {
    composable(RUTA_CATALOGOS) {
        CatalogosScreen(
            onPresupuestoClick = { categoriaId -> navController.navigate("$RUTA_PRESUPUESTO_DETALLE_BASE/$categoriaId") },
        )
    }
    composable(
        route = RUTA_PRESUPUESTO_DETALLE,
        arguments = listOf(navArgument("categoriaId") { type = NavType.LongType }),
    ) {
        PresupuestoDetalleScreen()
    }
}

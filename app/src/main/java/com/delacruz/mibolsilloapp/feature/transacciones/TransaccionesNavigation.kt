package com.delacruz.mibolsilloapp.feature.transacciones

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.delacruz.mibolsilloapp.feature.catalogos.RUTA_PRESUPUESTO_DETALLE_BASE

const val RUTA_RESUMEN = "resumen"
const val RUTA_CUENTAS = "cuentas"
const val RUTA_TRANSACCIONES = "transacciones"
const val RUTA_NEGOCIOS = "negocios"
const val RUTA_PROYECTOS = "proyectos"
const val RUTA_COMPRAS = "compras"
private const val RUTA_NEGOCIO_DETALLE = "negocios/{negocioId}"
private const val RUTA_PROYECTO_DETALLE = "proyectos/{proyectoId}"

fun NavGraphBuilder.resumenGraph(navController: NavController) {
    composable(RUTA_RESUMEN) {
        ResumenScreen(
            onPresupuestoClick = { categoriaId -> navController.navigate("$RUTA_PRESUPUESTO_DETALLE_BASE/$categoriaId") },
        )
    }
}

fun NavGraphBuilder.cuentasGraph() {
    composable(RUTA_CUENTAS) { CuentasScreen() }
}

fun NavGraphBuilder.transaccionesGraph() {
    composable(RUTA_TRANSACCIONES) { TransaccionesScreen() }
}

fun NavGraphBuilder.comprasGraph() {
    composable(RUTA_COMPRAS) { ComprasScreen() }
}

fun NavGraphBuilder.negociosGraph(navController: NavController) {
    composable(RUTA_NEGOCIOS) {
        NegociosScreen(onNegocioClick = { id -> navController.navigate("negocios/$id") })
    }
    composable(
        route = RUTA_NEGOCIO_DETALLE,
        arguments = listOf(navArgument("negocioId") { type = NavType.LongType }),
    ) {
        NegocioDetalleScreen()
    }
}

fun NavGraphBuilder.proyectosGraph(navController: NavController) {
    composable(RUTA_PROYECTOS) {
        ProyectosScreen(onProyectoClick = { id -> navController.navigate("proyectos/$id") })
    }
    composable(
        route = RUTA_PROYECTO_DETALLE,
        arguments = listOf(navArgument("proyectoId") { type = NavType.LongType }),
    ) {
        ProyectoDetalleScreen()
    }
}

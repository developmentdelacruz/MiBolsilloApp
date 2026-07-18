package com.delacruz.mibolsilloapp.feature.respaldo

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RUTA_RESPALDO = "respaldo"

fun NavGraphBuilder.respaldoGraph() {
    composable(RUTA_RESPALDO) { RespaldoScreen() }
}

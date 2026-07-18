package com.delacruz.mibolsilloapp.feature.gastoscompartidos

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RUTA_GASTOS_COMPARTIDOS = "gastos_compartidos"

fun NavGraphBuilder.gastosCompartidosGraph() {
    composable(RUTA_GASTOS_COMPARTIDOS) { GastosCompartidosScreen() }
}

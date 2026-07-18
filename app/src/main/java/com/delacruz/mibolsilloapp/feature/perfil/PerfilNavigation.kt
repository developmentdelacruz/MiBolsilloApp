package com.delacruz.mibolsilloapp.feature.perfil

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RUTA_PERFIL = "perfil"

fun NavGraphBuilder.perfilGraph() {
    composable(RUTA_PERFIL) { PerfilScreen() }
}

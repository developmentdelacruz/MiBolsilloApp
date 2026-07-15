package com.delacruz.mibolsilloapp.feature.catalogos

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val RUTA_CATALOGOS = "catalogos"

fun NavGraphBuilder.catalogosGraph() {
    composable(RUTA_CATALOGOS) { CatalogosScreen() }
}

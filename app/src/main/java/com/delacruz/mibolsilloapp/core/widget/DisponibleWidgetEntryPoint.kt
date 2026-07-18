package com.delacruz.mibolsilloapp.core.widget

import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.usecase.CalcularDisponibleParaGastarUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * GlanceAppWidget corre fuera del árbol de Composables de MainActivity (lo dibuja el proceso
 * del launcher vía RemoteViews), así que no hay hiltViewModel() disponible — este EntryPoint
 * es el puente para sacar dependencias del grafo de Hilt desde ahí.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface DisponibleWidgetEntryPoint {
    fun calcularDisponibleParaGastarUseCase(): CalcularDisponibleParaGastarUseCase
    fun monedaRepository(): MonedaRepository
}

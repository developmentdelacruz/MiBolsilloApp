package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConCosto
import com.delacruz.mibolsilloapp.domain.model.ProyectoConTransacciones
import kotlinx.coroutines.flow.Flow

interface ProyectoRepository {
    suspend fun crear(proyecto: Proyecto): Long
    suspend fun actualizar(proyecto: Proyecto)
    suspend fun eliminar(proyecto: Proyecto)
    fun observarTodos(): Flow<List<Proyecto>>
    fun observarConTransacciones(id: Long): Flow<ProyectoConTransacciones?>
    fun observarConCosto(id: Long): Flow<ProyectoConCosto?>
    fun observarTodosConCosto(): Flow<List<ProyectoConCosto>>
}

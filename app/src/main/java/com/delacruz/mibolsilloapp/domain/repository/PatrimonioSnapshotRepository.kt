package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.PatrimonioSnapshot
import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface PatrimonioSnapshotRepository {
    /** Inserta o reemplaza (por fecha) el snapshot del día. */
    suspend fun registrar(fecha: LocalDate, valor: BigDecimal)
    fun observarUltimos(limite: Int): Flow<List<PatrimonioSnapshot>>
}

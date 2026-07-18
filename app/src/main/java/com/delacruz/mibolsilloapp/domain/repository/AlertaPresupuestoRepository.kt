package com.delacruz.mibolsilloapp.domain.repository

import com.delacruz.mibolsilloapp.domain.model.NivelAlertaPresupuesto
import java.time.YearMonth

interface AlertaPresupuestoRepository {
    suspend fun yaSeAlerto(presupuestoId: Long, mes: YearMonth, nivel: NivelAlertaPresupuesto): Boolean
    suspend fun registrarAlerta(presupuestoId: Long, mes: YearMonth, nivel: NivelAlertaPresupuesto)
}

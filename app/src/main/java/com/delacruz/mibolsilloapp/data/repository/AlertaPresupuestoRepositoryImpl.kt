package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.AlertaPresupuestoDao
import com.delacruz.mibolsilloapp.data.local.entity.AlertaPresupuestoEntity
import com.delacruz.mibolsilloapp.domain.model.NivelAlertaPresupuesto
import com.delacruz.mibolsilloapp.domain.repository.AlertaPresupuestoRepository
import java.time.YearMonth
import javax.inject.Inject

class AlertaPresupuestoRepositoryImpl @Inject constructor(
    private val dao: AlertaPresupuestoDao,
) : AlertaPresupuestoRepository {

    override suspend fun yaSeAlerto(presupuestoId: Long, mes: YearMonth, nivel: NivelAlertaPresupuesto): Boolean =
        dao.existe(presupuestoId, mes.toString(), nivel.name)

    override suspend fun registrarAlerta(presupuestoId: Long, mes: YearMonth, nivel: NivelAlertaPresupuesto) {
        dao.insert(
            AlertaPresupuestoEntity(presupuestoId = presupuestoId, mes = mes.toString(), nivel = nivel.name),
        )
    }
}

package com.delacruz.mibolsilloapp.data.repository

import com.delacruz.mibolsilloapp.data.local.dao.PatrimonioSnapshotDao
import com.delacruz.mibolsilloapp.data.local.entity.PatrimonioSnapshotEntity
import com.delacruz.mibolsilloapp.data.mapper.toCentavos
import com.delacruz.mibolsilloapp.data.mapper.toDomain
import com.delacruz.mibolsilloapp.domain.model.PatrimonioSnapshot
import com.delacruz.mibolsilloapp.domain.repository.PatrimonioSnapshotRepository
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PatrimonioSnapshotRepositoryImpl @Inject constructor(
    private val dao: PatrimonioSnapshotDao,
) : PatrimonioSnapshotRepository {

    override suspend fun registrar(fecha: LocalDate, valor: BigDecimal) {
        dao.insert(PatrimonioSnapshotEntity(fecha = fecha, valorCentavos = valor.toCentavos()))
    }

    override fun observarUltimos(limite: Int): Flow<List<PatrimonioSnapshot>> =
        dao.observeUltimos(limite).map { entidades -> entidades.map { it.toDomain() } }
}

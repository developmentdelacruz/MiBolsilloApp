package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.dao.GastoMensualRow
import com.delacruz.mibolsilloapp.data.local.entity.TransaccionEntity
import com.delacruz.mibolsilloapp.domain.model.GastoMensual
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import java.time.YearMonth

fun TransaccionEntity.toDomain(): Transaccion = Transaccion(
    id = id,
    descripcion = descripcion,
    monto = montoCentavos.centavosToMonto(),
    fecha = fecha,
    tipo = tipo,
    categoriaId = categoriaId,
    cuentaId = cuentaId,
    negocioId = negocioId,
    proyectoId = proyectoId,
    compraId = compraId,
    numeroCuota = numeroCuota,
)

fun Transaccion.toEntity(): TransaccionEntity = TransaccionEntity(
    id = id,
    descripcion = descripcion,
    montoCentavos = monto.toCentavos(),
    fecha = fecha,
    tipo = tipo,
    categoriaId = categoriaId,
    cuentaId = cuentaId,
    negocioId = negocioId,
    proyectoId = proyectoId,
    compraId = compraId,
    numeroCuota = numeroCuota,
)

fun GastoMensualRow.toDomain(): GastoMensual = GastoMensual(
    mes = YearMonth.parse(mes),
    monto = montoCentavos.centavosToMonto(),
)

package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.CompromisoEntity
import com.delacruz.mibolsilloapp.data.local.entity.PagoCompromisoEntity
import com.delacruz.mibolsilloapp.data.local.relation.CompromisoConPagos as CompromisoConPagosRow
import com.delacruz.mibolsilloapp.data.local.relation.CompromisoConSaldo as CompromisoConSaldoRow
import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.CompromisoConPagos
import com.delacruz.mibolsilloapp.domain.model.CompromisoConSaldo
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso

fun CompromisoEntity.toDomain(): Compromiso = Compromiso(
    id = id,
    nombre = nombre,
    montoTotal = montoTotalCentavos.centavosToMonto(),
    cuotasTotales = cuotasTotales,
    diaPagoSugerido = diaPagoSugerido,
    estado = estado,
)

fun Compromiso.toEntity(): CompromisoEntity = CompromisoEntity(
    id = id,
    nombre = nombre,
    montoTotalCentavos = montoTotal.toCentavos(),
    cuotasTotales = cuotasTotales,
    diaPagoSugerido = diaPagoSugerido,
    estado = estado,
)

fun PagoCompromisoEntity.toDomain(): PagoCompromiso = PagoCompromiso(
    id = id,
    compromisoId = compromisoId,
    fechaPagoReal = fechaPagoReal,
    montoPagado = montoPagadoCentavos.centavosToMonto(),
    numeroCuota = numeroCuota,
    esAdelantado = esAdelantado,
)

fun PagoCompromiso.toEntity(): PagoCompromisoEntity = PagoCompromisoEntity(
    id = id,
    compromisoId = compromisoId,
    fechaPagoReal = fechaPagoReal,
    montoPagadoCentavos = montoPagado.toCentavos(),
    numeroCuota = numeroCuota,
    esAdelantado = esAdelantado,
)

fun CompromisoConSaldoRow.toDomain(): CompromisoConSaldo = CompromisoConSaldo(
    compromiso = compromiso.toDomain(),
    saldoPendiente = saldoPendienteCentavos.centavosToMonto(),
    cuotasPagadas = cuotasPagadas,
)

fun CompromisoConPagosRow.toDomain(): CompromisoConPagos = CompromisoConPagos(
    compromiso = compromiso.toDomain(),
    pagos = pagos.map { it.toDomain() },
)

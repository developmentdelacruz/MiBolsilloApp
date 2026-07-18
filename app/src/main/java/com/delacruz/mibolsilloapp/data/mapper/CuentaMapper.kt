package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.CuentaEntity
import com.delacruz.mibolsilloapp.data.local.relation.CuentaConSaldo as CuentaConSaldoRow
import com.delacruz.mibolsilloapp.domain.model.Cuenta
import com.delacruz.mibolsilloapp.domain.model.CuentaConSaldo

fun CuentaEntity.toDomain(): Cuenta = Cuenta(
    id = id,
    nombre = nombre,
    tipo = tipo,
    monedaId = monedaId,
    saldoInicial = saldoInicialCentavos.centavosToMonto(),
    activa = activa,
    limiteCredito = limiteCreditoCentavos?.centavosToMonto(),
)

fun Cuenta.toEntity(): CuentaEntity = CuentaEntity(
    id = id,
    nombre = nombre,
    tipo = tipo,
    monedaId = monedaId,
    saldoInicialCentavos = saldoInicial.toCentavos(),
    activa = activa,
    limiteCreditoCentavos = limiteCredito?.toCentavos(),
)

fun CuentaConSaldoRow.toDomain(): CuentaConSaldo = CuentaConSaldo(
    cuenta = cuenta.toDomain(),
    saldoActual = saldoActualCentavos.centavosToMonto(),
)

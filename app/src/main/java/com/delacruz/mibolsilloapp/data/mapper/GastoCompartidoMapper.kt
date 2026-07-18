package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.dao.GastoCompartidoConTransaccionRow
import com.delacruz.mibolsilloapp.data.local.entity.GastoCompartidoEntity
import com.delacruz.mibolsilloapp.domain.model.GastoCompartido
import com.delacruz.mibolsilloapp.domain.model.GastoCompartidoConTransaccion

fun GastoCompartidoEntity.toDomain(): GastoCompartido = GastoCompartido(
    id = id,
    transaccionId = transaccionId,
    nombreContacto = nombreContacto,
    telefono = telefono,
    montoAPagar = montoAPagarCentavos.centavosToMonto(),
    estadoPago = estadoPago,
)

fun GastoCompartido.toEntity(): GastoCompartidoEntity = GastoCompartidoEntity(
    id = id,
    transaccionId = transaccionId,
    nombreContacto = nombreContacto,
    telefono = telefono,
    montoAPagarCentavos = montoAPagar.toCentavos(),
    estadoPago = estadoPago,
)

fun GastoCompartidoConTransaccionRow.toDomain(): GastoCompartidoConTransaccion = GastoCompartidoConTransaccion(
    gasto = gasto.toDomain(),
    descripcionTransaccion = descripcionTransaccion,
    fechaTransaccion = fechaTransaccion,
)

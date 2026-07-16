package com.delacruz.mibolsilloapp.data.mapper

import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionCompartidaEntity
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionEntity
import com.delacruz.mibolsilloapp.data.local.relation.SuscripcionConInvitados as SuscripcionConInvitadosRow
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.model.SuscripcionCompartida
import com.delacruz.mibolsilloapp.domain.model.SuscripcionConInvitados

fun SuscripcionEntity.toDomain(): Suscripcion = Suscripcion(
    id = id,
    nombre = nombre,
    montoMensual = montoMensualCentavos.centavosToMonto(),
    diaCobro = diaCobro,
    categoriaId = categoriaId,
)

fun Suscripcion.toEntity(): SuscripcionEntity = SuscripcionEntity(
    id = id,
    nombre = nombre,
    montoMensualCentavos = montoMensual.toCentavos(),
    diaCobro = diaCobro,
    categoriaId = categoriaId,
)

fun SuscripcionCompartidaEntity.toDomain(): SuscripcionCompartida = SuscripcionCompartida(
    id = id,
    suscripcionId = suscripcionId,
    nombreContacto = nombreContacto,
    telefono = telefono,
    montoAPagar = montoAPagarCentavos.centavosToMonto(),
    estadoPago = estadoPago,
)

fun SuscripcionCompartida.toEntity(): SuscripcionCompartidaEntity = SuscripcionCompartidaEntity(
    id = id,
    suscripcionId = suscripcionId,
    nombreContacto = nombreContacto,
    telefono = telefono,
    montoAPagarCentavos = montoAPagar.toCentavos(),
    estadoPago = estadoPago,
)

fun SuscripcionConInvitadosRow.toDomain(): SuscripcionConInvitados = SuscripcionConInvitados(
    suscripcion = suscripcion.toDomain(),
    invitados = invitados.map { it.toDomain() },
)

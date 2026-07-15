package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionCompartidaEntity
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionEntity

data class SuscripcionConInvitados(
    @Embedded val suscripcion: SuscripcionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "suscripcionId",
    )
    val invitados: List<SuscripcionCompartidaEntity>,
)

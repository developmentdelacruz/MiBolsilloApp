package com.delacruz.mibolsilloapp.domain.model

data class SuscripcionConInvitados(
    val suscripcion: Suscripcion,
    val invitados: List<SuscripcionCompartida>,
)

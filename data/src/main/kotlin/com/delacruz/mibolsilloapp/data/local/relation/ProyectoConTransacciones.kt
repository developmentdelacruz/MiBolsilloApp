package com.delacruz.mibolsilloapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.delacruz.mibolsilloapp.data.local.entity.ProyectoEntity
import com.delacruz.mibolsilloapp.data.local.entity.TransaccionEntity

data class ProyectoConTransacciones(
    @Embedded val proyecto: ProyectoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "proyectoId",
    )
    val transacciones: List<TransaccionEntity>,
)

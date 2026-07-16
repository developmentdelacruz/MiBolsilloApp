package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.TipoCategoria

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val icono: String,
    val tipo: TipoCategoria,
)

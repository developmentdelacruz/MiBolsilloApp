package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.TipoCuenta

@Entity(
    tableName = "cuentas",
    foreignKeys = [
        ForeignKey(
            entity = MonedaEntity::class,
            parentColumns = ["id"],
            childColumns = ["monedaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("monedaId")],
)
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: TipoCuenta,
    val monedaId: Long,
    val saldoInicialCentavos: Long,
    val activa: Boolean = true,
    /** Solo relevante para TARJETA; habilita el % de utilización. */
    val limiteCreditoCentavos: Long? = null,
)

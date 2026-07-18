package com.delacruz.mibolsilloapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.delacruz.mibolsilloapp.domain.model.TipoTransaccion
import java.time.LocalDate

@Entity(
    tableName = "transacciones",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = NegocioEntity::class,
            parentColumns = ["id"],
            childColumns = ["negocioId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CompraEntity::class,
            parentColumns = ["id"],
            childColumns = ["compraId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("categoriaId"), Index("negocioId"), Index("proyectoId"), Index("cuentaId"), Index("compraId"),
    ],
)
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descripcion: String,
    val montoCentavos: Long,
    val fecha: LocalDate,
    val tipo: TipoTransaccion,
    val categoriaId: Long,
    val cuentaId: Long,
    val negocioId: Long? = null,
    val proyectoId: Long? = null,
    /** No nulo solo cuando la transacción fue generada por una compra en cuotas (ver CompraEntity). */
    val compraId: Long? = null,
    val numeroCuota: Int? = null,
)

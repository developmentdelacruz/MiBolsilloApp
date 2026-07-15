package com.delacruz.mibolsilloapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Transaccion(
    val id: Long = 0,
    val descripcion: String,
    val monto: BigDecimal,
    val fecha: LocalDate,
    val tipo: TipoTransaccion,
    val categoriaId: Long,
    val negocioId: Long? = null,
    val proyectoId: Long? = null,
)

enum class TipoTransaccion {
    INGRESO,
    GASTO,
}

package com.delacruz.mibolsilloapp.core.backup

import com.delacruz.mibolsilloapp.domain.model.Categoria
import com.delacruz.mibolsilloapp.domain.model.Compromiso
import com.delacruz.mibolsilloapp.domain.model.Moneda
import com.delacruz.mibolsilloapp.domain.model.Negocio
import com.delacruz.mibolsilloapp.domain.model.PagoCompromiso
import com.delacruz.mibolsilloapp.domain.model.Proyecto
import com.delacruz.mibolsilloapp.domain.model.Suscripcion
import com.delacruz.mibolsilloapp.domain.model.SuscripcionCompartida
import com.delacruz.mibolsilloapp.domain.model.Transaccion
import org.json.JSONArray
import org.json.JSONObject

internal fun <T> jsonArrayOf(items: List<T>, toJson: (T) -> JSONObject): JSONArray =
    JSONArray().apply { items.forEach { put(toJson(it)) } }

internal fun Categoria.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("nombre", nombre)
    .put("icono", icono)
    .put("tipo", tipo.name)

internal fun Moneda.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("codigo", codigo)
    .put("nombre", nombre)
    .put("simbolo", simbolo)
    .put("esPredeterminada", esPredeterminada)

internal fun Negocio.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("nombre", nombre)

internal fun Proyecto.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("nombre", nombre)
    .put("presupuestoEstimado", presupuestoEstimado.toPlainString())

internal fun Compromiso.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("nombre", nombre)
    .put("montoTotal", montoTotal.toPlainString())
    .put("cuotasTotales", cuotasTotales)
    .put("diaPagoSugerido", diaPagoSugerido)
    .put("estado", estado.name)

internal fun PagoCompromiso.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("compromisoId", compromisoId)
    .put("fechaPagoReal", fechaPagoReal.toString())
    .put("montoPagado", montoPagado.toPlainString())
    .put("numeroCuota", numeroCuota)
    .put("esAdelantado", esAdelantado)

internal fun Suscripcion.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("nombre", nombre)
    .put("montoMensual", montoMensual.toPlainString())
    .put("diaCobro", diaCobro)
    .put("categoriaId", categoriaId)

internal fun SuscripcionCompartida.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("suscripcionId", suscripcionId)
    .put("nombreContacto", nombreContacto)
    .put("telefono", telefono)
    .put("montoAPagar", montoAPagar.toPlainString())
    .put("estadoPago", estadoPago.name)

internal fun Transaccion.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("descripcion", descripcion)
    .put("monto", monto.toPlainString())
    .put("fecha", fecha.toString())
    .put("tipo", tipo.name)
    .put("categoriaId", categoriaId)
    .put("negocioId", negocioId ?: JSONObject.NULL)
    .put("proyectoId", proyectoId ?: JSONObject.NULL)

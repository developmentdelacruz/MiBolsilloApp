package com.delacruz.mibolsilloapp.core.backup

import android.content.Context
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import org.json.JSONObject

class BackupManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoriaRepository: CategoriaRepository,
    private val monedaRepository: MonedaRepository,
    private val negocioRepository: NegocioRepository,
    private val proyectoRepository: ProyectoRepository,
    private val compromisoRepository: CompromisoRepository,
    private val suscripcionRepository: SuscripcionRepository,
    private val transaccionRepository: TransaccionRepository,
) : BackupManager {

    override suspend fun exportarRespaldo(): Result<File> = runCatching {
        val compromisos = compromisoRepository.observarTodos().first()
        val pagos = compromisos.flatMap { compromiso ->
            compromisoRepository.observarConPagos(compromiso.id).first()?.pagos.orEmpty()
        }

        val suscripciones = suscripcionRepository.observarTodas().first()
        val invitados = suscripciones.flatMap { suscripcion ->
            suscripcionRepository.observarConInvitados(suscripcion.id).first()?.invitados.orEmpty()
        }

        val raiz = JSONObject()
            .put("version", 1)
            .put("fecha", LocalDateTime.now().toString())
            .put("categorias", jsonArrayOf(categoriaRepository.observarTodas().first()) { it.toJson() })
            .put("monedas", jsonArrayOf(monedaRepository.observarTodas().first()) { it.toJson() })
            .put("negocios", jsonArrayOf(negocioRepository.observarTodos().first()) { it.toJson() })
            .put("proyectos", jsonArrayOf(proyectoRepository.observarTodos().first()) { it.toJson() })
            .put("compromisos", jsonArrayOf(compromisos) { it.toJson() })
            .put("pagosCompromisos", jsonArrayOf(pagos) { it.toJson() })
            .put("suscripciones", jsonArrayOf(suscripciones) { it.toJson() })
            .put("suscripcionesCompartidas", jsonArrayOf(invitados) { it.toJson() })
            .put("transacciones", jsonArrayOf(transaccionRepository.observarTodas().first()) { it.toJson() })

        val carpeta = File(context.filesDir, "backups").apply { mkdirs() }
        val archivo = File(carpeta, "finanzas360_${System.currentTimeMillis()}.json")
        archivo.writeText(raiz.toString(2))
        archivo
    }

    override fun listarRespaldos(): List<File> =
        File(context.filesDir, "backups")
            .listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
}

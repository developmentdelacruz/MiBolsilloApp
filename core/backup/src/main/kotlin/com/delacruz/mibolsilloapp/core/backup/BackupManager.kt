package com.delacruz.mibolsilloapp.core.backup

import java.io.File

/**
 * Exporta toda la base local a un JSON en almacenamiento propio de la app.
 * No sube nada a Google Drive: el doc pide dejarlo "preparado para que el usuario
 * pueda subirlo manualmente" — el archivo resultante es apto para compartir con un
 * Intent.ACTION_SEND desde la UI (fuera de este módulo).
 */
interface BackupManager {
    suspend fun exportarRespaldo(): Result<File>
    fun listarRespaldos(): List<File>
}

package com.delacruz.mibolsilloapp.core.backup

import android.net.Uri

interface RespaldoManager {
    /** Copia el archivo de base de datos completo al destino elegido por el usuario. */
    suspend fun exportarA(destino: Uri): Result<Unit>

    /** Reemplaza la base de datos actual por el archivo elegido. Requiere reiniciar la app. */
    suspend fun restaurarDesde(origen: Uri): Result<Unit>
}

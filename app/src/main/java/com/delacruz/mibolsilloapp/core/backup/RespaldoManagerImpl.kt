package com.delacruz.mibolsilloapp.core.backup

import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import com.delacruz.mibolsilloapp.data.local.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RespaldoManagerImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    @ApplicationContext private val context: Context,
) : RespaldoManager {

    override suspend fun exportarA(destino: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            checkpointWal()
            val archivoDb = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            context.contentResolver.openOutputStream(destino)?.use { salida ->
                archivoDb.inputStream().use { entrada -> entrada.copyTo(salida) }
            } ?: throw IOException("No se pudo abrir el destino para escribir el respaldo")
            Unit
        }
    }

    override suspend fun restaurarDesde(origen: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val archivoDb = context.getDatabasePath(AppDatabase.DATABASE_NAME)
            val temporal = File.createTempFile("respaldo_restaurar", ".db", context.cacheDir)
            try {
                context.contentResolver.openInputStream(origen)?.use { entrada ->
                    temporal.outputStream().use { salida -> entrada.copyTo(salida) }
                } ?: throw IOException("No se pudo abrir el archivo seleccionado")

                if (!pareceBaseSqlite(temporal)) {
                    throw IOException("El archivo seleccionado no es un respaldo de base de datos válido")
                }

                temporal.copyTo(archivoDb, overwrite = true)
                eliminarSidecars(archivoDb)
            } finally {
                temporal.delete()
            }
        }
    }

    /**
     * Fuerza el volcado del WAL (write-ahead log) al archivo principal antes de copiarlo —
     * si no, una exportación puede quedar sin los cambios más recientes, que a veces viven
     * solo en los archivos -wal/-shm hasta que SQLite hace checkpoint por su cuenta.
     */
    private fun checkpointWal() {
        try {
            appDatabase.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(TRUNCATE)").use { it.moveToFirst() }
        } catch (e: SQLiteException) {
            // La base no está en modo WAL: no hay nada que volcar.
        }
    }

    private fun pareceBaseSqlite(archivo: File): Boolean {
        if (archivo.length() < CABECERA_SQLITE.size) return false
        val cabecera = ByteArray(CABECERA_SQLITE.size)
        archivo.inputStream().use { it.read(cabecera) }
        return cabecera.contentEquals(CABECERA_SQLITE)
    }

    /**
     * Si quedan -wal/-shm de la base anterior, Room intentaría reproducir ese log sobre el
     * archivo recién restaurado en el próximo arranque — hay que borrarlos para que la base
     * restaurada se abra tal cual está, sin mezclarse con escrituras de la base reemplazada.
     */
    private fun eliminarSidecars(archivoDb: File) {
        File(archivoDb.path + "-wal").delete()
        File(archivoDb.path + "-shm").delete()
        File(archivoDb.path + "-journal").delete()
    }

    private companion object {
        /** Los 16 bytes con los que SQLite siempre arranca un archivo de base de datos válido. */
        val CABECERA_SQLITE = byteArrayOf(
            'S'.code.toByte(), 'Q'.code.toByte(), 'L'.code.toByte(), 'i'.code.toByte(),
            't'.code.toByte(), 'e'.code.toByte(), ' '.code.toByte(), 'f'.code.toByte(),
            'o'.code.toByte(), 'r'.code.toByte(), 'm'.code.toByte(), 'a'.code.toByte(),
            't'.code.toByte(), ' '.code.toByte(), '3'.code.toByte(), 0,
        )
    }
}

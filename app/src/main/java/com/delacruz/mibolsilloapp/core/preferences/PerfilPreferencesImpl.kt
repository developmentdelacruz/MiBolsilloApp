package com.delacruz.mibolsilloapp.core.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerfilPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PerfilPreferences {

    override suspend fun obtenerNombre(): String? = withContext(Dispatchers.IO) {
        prefs().getString(CLAVE_NOMBRE, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun guardarNombre(nombre: String) = withContext(Dispatchers.IO) {
        prefs().edit().putString(CLAVE_NOMBRE, nombre).apply()
    }

    private fun prefs() = context.getSharedPreferences(PREFS_PERFIL, Context.MODE_PRIVATE)

    private companion object {
        const val PREFS_PERFIL = "perfil"
        const val CLAVE_NOMBRE = "nombre"
    }
}

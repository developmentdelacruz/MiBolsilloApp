package com.delacruz.mibolsilloapp.core.preferences

interface PerfilPreferences {
    suspend fun obtenerNombre(): String?
    suspend fun guardarNombre(nombre: String)
}

package com.delacruz.mibolsilloapp.data.local.converter

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Room no soporta LocalDate de forma nativa (los enums sí, desde Room 2.3,
 * por eso no llevan converter aquí). Los montos se guardan como Long
 * (centavos) para que SUM() en SQLite sea aritmética entera exacta.
 */
class Converters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
}

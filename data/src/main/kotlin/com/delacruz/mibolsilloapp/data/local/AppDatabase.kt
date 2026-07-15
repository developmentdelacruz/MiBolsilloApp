package com.delacruz.mibolsilloapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.delacruz.mibolsilloapp.data.local.converter.Converters
import com.delacruz.mibolsilloapp.data.local.dao.CategoriaDao
import com.delacruz.mibolsilloapp.data.local.dao.CompromisoDao
import com.delacruz.mibolsilloapp.data.local.dao.MonedaDao
import com.delacruz.mibolsilloapp.data.local.dao.NegocioDao
import com.delacruz.mibolsilloapp.data.local.dao.ProyectoDao
import com.delacruz.mibolsilloapp.data.local.dao.SuscripcionDao
import com.delacruz.mibolsilloapp.data.local.dao.TransaccionDao
import com.delacruz.mibolsilloapp.data.local.entity.CategoriaEntity
import com.delacruz.mibolsilloapp.data.local.entity.CompromisoEntity
import com.delacruz.mibolsilloapp.data.local.entity.MonedaEntity
import com.delacruz.mibolsilloapp.data.local.entity.NegocioEntity
import com.delacruz.mibolsilloapp.data.local.entity.PagoCompromisoEntity
import com.delacruz.mibolsilloapp.data.local.entity.ProyectoEntity
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionCompartidaEntity
import com.delacruz.mibolsilloapp.data.local.entity.SuscripcionEntity
import com.delacruz.mibolsilloapp.data.local.entity.TransaccionEntity

@Database(
    entities = [
        CategoriaEntity::class,
        MonedaEntity::class,
        NegocioEntity::class,
        ProyectoEntity::class,
        CompromisoEntity::class,
        PagoCompromisoEntity::class,
        SuscripcionEntity::class,
        SuscripcionCompartidaEntity::class,
        TransaccionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun monedaDao(): MonedaDao
    abstract fun negocioDao(): NegocioDao
    abstract fun proyectoDao(): ProyectoDao
    abstract fun compromisoDao(): CompromisoDao
    abstract fun suscripcionDao(): SuscripcionDao
    abstract fun transaccionDao(): TransaccionDao

    companion object {
        const val DATABASE_NAME = "finanzas360.db"
    }
}

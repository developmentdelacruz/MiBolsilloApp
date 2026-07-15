package com.delacruz.mibolsilloapp.data.di

import android.content.Context
import androidx.room.Room
import com.delacruz.mibolsilloapp.data.local.AppDatabase
import com.delacruz.mibolsilloapp.data.local.dao.CategoriaDao
import com.delacruz.mibolsilloapp.data.local.dao.CompromisoDao
import com.delacruz.mibolsilloapp.data.local.dao.MonedaDao
import com.delacruz.mibolsilloapp.data.local.dao.NegocioDao
import com.delacruz.mibolsilloapp.data.local.dao.ProyectoDao
import com.delacruz.mibolsilloapp.data.local.dao.SuscripcionDao
import com.delacruz.mibolsilloapp.data.local.dao.TransaccionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideCategoriaDao(db: AppDatabase): CategoriaDao = db.categoriaDao()

    @Provides
    fun provideMonedaDao(db: AppDatabase): MonedaDao = db.monedaDao()

    @Provides
    fun provideNegocioDao(db: AppDatabase): NegocioDao = db.negocioDao()

    @Provides
    fun provideProyectoDao(db: AppDatabase): ProyectoDao = db.proyectoDao()

    @Provides
    fun provideCompromisoDao(db: AppDatabase): CompromisoDao = db.compromisoDao()

    @Provides
    fun provideSuscripcionDao(db: AppDatabase): SuscripcionDao = db.suscripcionDao()

    @Provides
    fun provideTransaccionDao(db: AppDatabase): TransaccionDao = db.transaccionDao()
}

package com.delacruz.mibolsilloapp.data.di

import android.content.Context
import androidx.room.Room
import com.delacruz.mibolsilloapp.data.local.AppDatabase
import com.delacruz.mibolsilloapp.data.local.dao.AlertaPresupuestoDao
import com.delacruz.mibolsilloapp.data.local.dao.CategoriaDao
import com.delacruz.mibolsilloapp.data.local.dao.CompraDao
import com.delacruz.mibolsilloapp.data.local.dao.CompromisoDao
import com.delacruz.mibolsilloapp.data.local.dao.CuentaDao
import com.delacruz.mibolsilloapp.data.local.dao.GastoCompartidoDao
import com.delacruz.mibolsilloapp.data.local.dao.MonedaDao
import com.delacruz.mibolsilloapp.data.local.dao.NegocioDao
import com.delacruz.mibolsilloapp.data.local.dao.PatrimonioSnapshotDao
import com.delacruz.mibolsilloapp.data.local.dao.PresupuestoDao
import com.delacruz.mibolsilloapp.data.local.dao.ProyectoDao
import com.delacruz.mibolsilloapp.data.local.dao.SuscripcionDao
import com.delacruz.mibolsilloapp.data.local.dao.TransaccionDao
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_1_2
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_2_3
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_3_4
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_4_5
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_5_6
import com.delacruz.mibolsilloapp.data.local.migration.MIGRATION_6_7
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
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Migraciones reales: el usuario ya tiene datos reales cargados, así que un bump
            // de versión ya NO puede borrar la base. Si algún día falta la Migration de una
            // versión nueva, se prefiere que la app crashee (se nota al toque) a que borre
            // datos en silencio con fallbackToDestructiveMigration.
            .addMigrations(
                MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7,
            )
            .build()

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

    @Provides
    fun provideCuentaDao(db: AppDatabase): CuentaDao = db.cuentaDao()

    @Provides
    fun providePresupuestoDao(db: AppDatabase): PresupuestoDao = db.presupuestoDao()

    @Provides
    fun provideCompraDao(db: AppDatabase): CompraDao = db.compraDao()

    @Provides
    fun providePatrimonioSnapshotDao(db: AppDatabase): PatrimonioSnapshotDao = db.patrimonioSnapshotDao()

    @Provides
    fun provideAlertaPresupuestoDao(db: AppDatabase): AlertaPresupuestoDao = db.alertaPresupuestoDao()

    @Provides
    fun provideGastoCompartidoDao(db: AppDatabase): GastoCompartidoDao = db.gastoCompartidoDao()
}

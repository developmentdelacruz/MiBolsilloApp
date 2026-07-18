package com.delacruz.mibolsilloapp.data.di

import com.delacruz.mibolsilloapp.data.repository.AlertaPresupuestoRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.CategoriaRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.CompraRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.CompromisoRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.CuentaRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.GastoCompartidoRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.MonedaRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.NegocioRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.PatrimonioSnapshotRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.PresupuestoRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.ProyectoRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.SuscripcionRepositoryImpl
import com.delacruz.mibolsilloapp.data.repository.TransaccionRepositoryImpl
import com.delacruz.mibolsilloapp.domain.repository.AlertaPresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.CategoriaRepository
import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.GastoCompartidoRepository
import com.delacruz.mibolsilloapp.domain.repository.MonedaRepository
import com.delacruz.mibolsilloapp.domain.repository.NegocioRepository
import com.delacruz.mibolsilloapp.domain.repository.PatrimonioSnapshotRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.ProyectoRepository
import com.delacruz.mibolsilloapp.domain.repository.SuscripcionRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoriaRepository(impl: CategoriaRepositoryImpl): CategoriaRepository

    @Binds
    @Singleton
    abstract fun bindMonedaRepository(impl: MonedaRepositoryImpl): MonedaRepository

    @Binds
    @Singleton
    abstract fun bindNegocioRepository(impl: NegocioRepositoryImpl): NegocioRepository

    @Binds
    @Singleton
    abstract fun bindProyectoRepository(impl: ProyectoRepositoryImpl): ProyectoRepository

    @Binds
    @Singleton
    abstract fun bindCompromisoRepository(impl: CompromisoRepositoryImpl): CompromisoRepository

    @Binds
    @Singleton
    abstract fun bindSuscripcionRepository(impl: SuscripcionRepositoryImpl): SuscripcionRepository

    @Binds
    @Singleton
    abstract fun bindTransaccionRepository(impl: TransaccionRepositoryImpl): TransaccionRepository

    @Binds
    @Singleton
    abstract fun bindCuentaRepository(impl: CuentaRepositoryImpl): CuentaRepository

    @Binds
    @Singleton
    abstract fun bindPresupuestoRepository(impl: PresupuestoRepositoryImpl): PresupuestoRepository

    @Binds
    @Singleton
    abstract fun bindCompraRepository(impl: CompraRepositoryImpl): CompraRepository

    @Binds
    @Singleton
    abstract fun bindPatrimonioSnapshotRepository(
        impl: PatrimonioSnapshotRepositoryImpl,
    ): PatrimonioSnapshotRepository

    @Binds
    @Singleton
    abstract fun bindAlertaPresupuestoRepository(impl: AlertaPresupuestoRepositoryImpl): AlertaPresupuestoRepository

    @Binds
    @Singleton
    abstract fun bindGastoCompartidoRepository(impl: GastoCompartidoRepositoryImpl): GastoCompartidoRepository
}

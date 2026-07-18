package com.delacruz.mibolsilloapp.data.di

import com.delacruz.mibolsilloapp.domain.repository.CompraRepository
import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
import com.delacruz.mibolsilloapp.domain.repository.CuentaRepository
import com.delacruz.mibolsilloapp.domain.repository.PresupuestoRepository
import com.delacruz.mibolsilloapp.domain.repository.TransaccionRepository
import com.delacruz.mibolsilloapp.domain.usecase.CalcularDisponibleParaGastarUseCase
import com.delacruz.mibolsilloapp.domain.usecase.CalcularPatrimonioNetoUseCase
import com.delacruz.mibolsilloapp.domain.usecase.CalcularRolloverPresupuestoUseCase
import com.delacruz.mibolsilloapp.domain.usecase.DetectarTransaccionesRecurrentesUseCase
import com.delacruz.mibolsilloapp.domain.usecase.EliminarCompraUseCase
import com.delacruz.mibolsilloapp.domain.usecase.GenerarCuotasCompraUseCase
import com.delacruz.mibolsilloapp.domain.usecase.RegistrarPagoCompromisoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRegistrarPagoCompromisoUseCase(
        compromisoRepository: CompromisoRepository,
    ): RegistrarPagoCompromisoUseCase = RegistrarPagoCompromisoUseCase(compromisoRepository)

    @Provides
    fun provideCalcularDisponibleParaGastarUseCase(
        cuentaRepository: CuentaRepository,
        compromisoRepository: CompromisoRepository,
        presupuestoRepository: PresupuestoRepository,
    ): CalcularDisponibleParaGastarUseCase = CalcularDisponibleParaGastarUseCase(
        cuentaRepository,
        compromisoRepository,
        presupuestoRepository,
    )

    @Provides
    fun provideCalcularPatrimonioNetoUseCase(
        cuentaRepository: CuentaRepository,
        compromisoRepository: CompromisoRepository,
    ): CalcularPatrimonioNetoUseCase = CalcularPatrimonioNetoUseCase(cuentaRepository, compromisoRepository)

    @Provides
    fun provideCalcularRolloverPresupuestoUseCase(
        transaccionRepository: TransaccionRepository,
    ): CalcularRolloverPresupuestoUseCase = CalcularRolloverPresupuestoUseCase(transaccionRepository)

    @Provides
    fun provideDetectarTransaccionesRecurrentesUseCase(
        transaccionRepository: TransaccionRepository,
    ): DetectarTransaccionesRecurrentesUseCase = DetectarTransaccionesRecurrentesUseCase(transaccionRepository)

    @Provides
    fun provideGenerarCuotasCompraUseCase(
        compraRepository: CompraRepository,
        transaccionRepository: TransaccionRepository,
    ): GenerarCuotasCompraUseCase = GenerarCuotasCompraUseCase(compraRepository, transaccionRepository)

    @Provides
    fun provideEliminarCompraUseCase(
        compraRepository: CompraRepository,
        transaccionRepository: TransaccionRepository,
    ): EliminarCompraUseCase = EliminarCompraUseCase(compraRepository, transaccionRepository)
}

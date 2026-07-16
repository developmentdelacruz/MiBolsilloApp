package com.delacruz.mibolsilloapp.data.di

import com.delacruz.mibolsilloapp.domain.repository.CompromisoRepository
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
}

package com.delacruz.mibolsilloapp.core.preferences.di

import com.delacruz.mibolsilloapp.core.preferences.PerfilPreferences
import com.delacruz.mibolsilloapp.core.preferences.PerfilPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindPerfilPreferences(impl: PerfilPreferencesImpl): PerfilPreferences
}

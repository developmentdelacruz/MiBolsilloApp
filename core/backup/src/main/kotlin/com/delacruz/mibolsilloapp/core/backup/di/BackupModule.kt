package com.delacruz.mibolsilloapp.core.backup.di

import com.delacruz.mibolsilloapp.core.backup.BackupManager
import com.delacruz.mibolsilloapp.core.backup.BackupManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {

    @Binds
    @Singleton
    abstract fun bindBackupManager(impl: BackupManagerImpl): BackupManager
}

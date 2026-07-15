package com.delacruz.mibolsilloapp.core.notification.di

import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifier
import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifierImpl
import com.delacruz.mibolsilloapp.core.notification.scheduler.NotificationScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.NotificationSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindPaymentNotifier(impl: PaymentNotifierImpl): PaymentNotifier

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(impl: NotificationSchedulerImpl): NotificationScheduler
}

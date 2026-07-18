package com.delacruz.mibolsilloapp.core.notification.di

import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifier
import com.delacruz.mibolsilloapp.core.notification.notifier.PaymentNotifierImpl
import com.delacruz.mibolsilloapp.core.notification.scheduler.AlertaPresupuestoScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.AlertaPresupuestoSchedulerImpl
import com.delacruz.mibolsilloapp.core.notification.scheduler.NotificationScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.NotificationSchedulerImpl
import com.delacruz.mibolsilloapp.core.notification.scheduler.PatrimonioScheduler
import com.delacruz.mibolsilloapp.core.notification.scheduler.PatrimonioSchedulerImpl
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

    @Binds
    @Singleton
    abstract fun bindPatrimonioScheduler(impl: PatrimonioSchedulerImpl): PatrimonioScheduler

    @Binds
    @Singleton
    abstract fun bindAlertaPresupuestoScheduler(impl: AlertaPresupuestoSchedulerImpl): AlertaPresupuestoScheduler
}

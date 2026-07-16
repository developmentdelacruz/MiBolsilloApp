package com.delacruz.mibolsilloapp.core.notification.notifier

interface PaymentNotifier {
    fun notificarPagoProximo(id: Long, titulo: String, mensaje: String)
}

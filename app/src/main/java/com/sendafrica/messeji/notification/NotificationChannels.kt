package com.sendafrica.messeji.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {
    const val CHANNEL_MTU_KWA_MTU = "mtu_kwa_mtu"
    const val CHANNEL_PESA_OTP = "pesa_otp"
    const val CHANNEL_MATANGAZO = "matangazo"
    const val CHANNEL_SERVICE = "service"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channels = listOf(
            NotificationChannel(
                CHANNEL_MTU_KWA_MTU,
                "Mtu kwa Mtu",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Meseji kutoka kwa watu"
                enableVibration(true)
                setShowBadge(true)
            },
            NotificationChannel(
                CHANNEL_PESA_OTP,
                "Pesa na OTP",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Meseji za pesa na namba za uthibitisho"
                enableVibration(true)
                setShowBadge(true)
            },
            NotificationChannel(
                CHANNEL_MATANGAZO,
                "Matangazo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Matangazo na meseji za utangazaji"
                enableVibration(false)
                setShowBadge(false)
            },
            NotificationChannel(
                CHANNEL_SERVICE,
                "Huduma",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Arifa za huduma"
            }
        )

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { manager.createNotificationChannel(it) }
    }
}

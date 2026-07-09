package com.sendafrica.messeji.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.sendafrica.messeji.MainActivity
import com.sendafrica.messeji.data.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettings: AppSettings
) {
    private var notificationId = 1000

    fun showMessageNotification(
        context: Context,
        sender: String,
        contactName: String,
        body: String,
        threadId: Long,
        category: String
    ) {
        val prefs = runBlocking { appSettings.preferences.first() }
        if (!prefs.notificationsEnabled) return

        val channelId = when (category) {
            "money_otp" -> {
                if (!prefs.notifPesaOtp) return
                NotificationChannels.CHANNEL_PESA_OTP
            }
            "promo" -> {
                if (!prefs.notifMatangazo) return
                NotificationChannels.CHANNEL_MATANGAZO
            }
            else -> {
                if (!prefs.notifMtuKwaMtu) return
                NotificationChannels.CHANNEL_MTU_KWA_MTU
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("thread_id", threadId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, threadId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val replyIntent = Intent(context, MainActivity::class.java).apply {
            action = "ACTION_REPLY"
            putExtra("thread_id", threadId)
            putExtra("address", sender)
        }

        val replyPendingIntent = PendingIntent.getActivity(
            context, threadId.toInt() + 1000, replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val markReadIntent = Intent(context, MainActivity::class.java).apply {
            action = "ACTION_MARK_READ"
            putExtra("thread_id", threadId)
        }

        val markReadPendingIntent = PendingIntent.getActivity(
            context, threadId.toInt() + 2000, markReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val person = Person.Builder()
            .setName(contactName)
            .build()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(contactName)
            .setContentText(body)
            .setStyle(NotificationCompat.MessagingStyle(person)
                .addMessage(body, System.currentTimeMillis(), person))
            .setPriority(
                when (category) {
                    "promo" -> NotificationCompat.PRIORITY_LOW
                    else -> NotificationCompat.PRIORITY_HIGH
                }
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_menu_revert,
                    "Jibu",
                    replyPendingIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_menu_edit,
                    "Soma",
                    markReadPendingIntent
                ).build()
            )
            .setGroup("messeji_$threadId")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
            .build()

        NotificationManagerCompat.from(context).notify(
            notificationId++, notification
        )
    }
}

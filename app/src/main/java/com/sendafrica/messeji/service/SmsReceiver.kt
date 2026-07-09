package com.sendafrica.messeji.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import com.sendafrica.messeji.data.categorization.CategoryEngine
import com.sendafrica.messeji.data.db.AppDatabase
import com.sendafrica.messeji.data.db.entity.MessageMeta
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.notification.MessageNotificationManager
import com.sendafrica.messeji.util.ContactResolver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var categoryEngine: CategoryEngine

    @Inject
    lateinit var contactResolver: ContactResolver

    @Inject
    lateinit var notificationManager: MessageNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Telephony.Sms.Intents.getMessagesFromIntent(intent)
            } else {
                val bundle = intent.extras
                val pdus = bundle?.get("pdus") as? Array<*>
                pdus?.mapNotNull {
                    SmsMessage.createFromPdu(it as ByteArray)
                }?.toTypedArray() ?: emptyArray()
            }

            for (sms in messages) {
                handleIncomingSms(context, sms)
            }
        }
    }

    private fun handleIncomingSms(context: Context, sms: SmsMessage) {
        val sender = sms.displayOriginatingAddress ?: return
        val body = sms.displayMessageBody ?: return
        val timestamp = sms.timestampMillis

        CoroutineScope(Dispatchers.IO).launch {
            categoryEngine.loadBundledRules()
            val isContact = contactResolver.isContact(sender)
            val category = categoryEngine.categorize(sender, body, isContact)

            val threadId = getOrCreateThreadId(sender)

            database.messageMetaDao().upsert(
                MessageMeta(
                    messageId = threadId,
                    category = category.value,
                    isManualOverride = false
                )
            )

            notificationManager.showMessageNotification(
                context = context,
                sender = sender,
                contactName = contactResolver.resolveContact(sender)?.name ?: sender,
                body = body,
                threadId = threadId,
                category = category.value
            )
        }
    }

    private suspend fun getOrCreateThreadId(address: String): Long {
        return address.hashCode().toLong() and 0x7FFFFFFF
    }
}

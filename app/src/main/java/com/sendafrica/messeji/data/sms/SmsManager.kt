package com.sendafrica.messeji.data.sms

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.telephony.SmsManager as TelephonySmsManager
import android.text.TextUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class SmsThread(
    val threadId: Long,
    val address: String,
    val contactName: String?,
    val snippet: String?,
    val date: Long,
    val messageCount: Int,
    val read: Boolean,
    val hasAttachment: Boolean
)

data class SmsMessage(
    val id: Long,
    val threadId: Long,
    val address: String,
    val contactName: String?,
    val body: String?,
    val date: Long,
    val dateSent: Long,
    val type: Int,
    val read: Boolean,
    val status: Int,
    val subscriptionId: Int = 0
)

@Singleton
class SmsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun getAllThreads(): List<SmsThread> {
        val threads = mutableListOf<SmsThread>()
        val cursor = contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI.buildUpon()
                .appendQueryParameter("simple", "true").build(),
            arrayOf("thread_id", "address", "body", "date", "read"),
            null, null, "date DESC"
        )
        cursor?.use { c ->
            while (c.moveToNext()) {
                val threadId = c.getLong(c.getColumnIndexOrThrow("thread_id"))
                val address = c.getString(c.getColumnIndexOrThrow("address")) ?: ""
                val body = c.getString(c.getColumnIndexOrThrow("body"))
                val date = c.getLong(c.getColumnIndexOrThrow("date"))
                val read = c.getInt(c.getColumnIndexOrThrow("read")) == 1

                val existingIndex = threads.indexOfFirst { it.threadId == threadId }
                if (existingIndex >= 0) {
                    val existing = threads[existingIndex]
                    threads[existingIndex] = existing.copy(
                        messageCount = existing.messageCount + 1,
                        date = maxOf(existing.date, date)
                    )
                } else {
                    threads.add(
                        SmsThread(
                            threadId = threadId,
                            address = address,
                            contactName = null,
                            snippet = body,
                            date = date,
                            messageCount = 1,
                            read = read,
                            hasAttachment = false
                        )
                    )
                }
            }
        }
        return threads
    }

    fun getThreadMessages(threadId: Long): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            "thread_id = ?",
            arrayOf(threadId.toString()),
            "date ASC"
        )
        cursor?.use { c ->
            while (c.moveToNext()) {
                messages.add(c.toSmsMessage())
            }
        }
        return messages
    }

    fun observeThreadMessages(threadId: Long): List<SmsMessage> {
        return getThreadMessages(threadId)
    }

    fun getMessageById(messageId: Long): SmsMessage? {
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            "_id = ?",
            arrayOf(messageId.toString()),
            null
        )
        cursor?.use { c ->
            if (c.moveToFirst()) return c.toSmsMessage()
        }
        return null
    }

    fun getThreadCount(): Int {
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf("DISTINCT thread_id"),
            null, null, null
        )
        return cursor?.count ?: 0
    }

    fun sendSms(
        address: String,
        body: String,
        subscriptionId: Int = 0
    ): Boolean {
        return try {
            val smsManager = if (subscriptionId > 0) {
                TelephonySmsManager.getSmsManagerForSubscriptionId(subscriptionId)
            } else {
                TelephonySmsManager.getDefault()
            }
            smsManager.sendTextMessage(address, null, body, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun sendMultipartSms(
        address: String,
        body: String,
        subscriptionId: Int = 0
    ): Boolean {
        return try {
            val smsManager = if (subscriptionId > 0) {
                TelephonySmsManager.getSmsManagerForSubscriptionId(subscriptionId)
            } else {
                TelephonySmsManager.getDefault()
            }
            val parts = smsManager.divideMessage(body)
            smsManager.sendMultipartTextMessage(address, null, parts, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun markMessageRead(messageId: Long) {
        val values = android.content.ContentValues().apply {
            put("read", 1)
        }
        contentResolver.update(
            Telephony.Sms.CONTENT_URI,
            values,
            "_id = ?",
            arrayOf(messageId.toString())
        )
    }

    fun deleteMessage(messageId: Long) {
        contentResolver.delete(
            Telephony.Sms.CONTENT_URI,
            "_id = ?",
            arrayOf(messageId.toString())
        )
    }

    fun markThreadRead(threadId: Long) {
        val values = android.content.ContentValues().apply {
            put("read", 1)
        }
        contentResolver.update(
            Telephony.Sms.CONTENT_URI,
            values,
            "thread_id = ?",
            arrayOf(threadId.toString())
        )
    }

    fun deleteThread(threadId: Long) {
        contentResolver.delete(
            Telephony.Sms.CONTENT_URI,
            "thread_id = ?",
            arrayOf(threadId.toString())
        )
    }

    private fun Cursor.toSmsMessage(): SmsMessage {
        return SmsMessage(
            id = getLong(getColumnIndexOrThrow("_id")),
            threadId = getLong(getColumnIndexOrThrow("thread_id")),
            address = getString(getColumnIndexOrThrow("address")) ?: "",
            contactName = null,
            body = getString(getColumnIndexOrThrow("body")),
            date = getLong(getColumnIndexOrThrow("date")),
            dateSent = getLong(getColumnIndexOrThrow("date_sent")),
            type = getInt(getColumnIndexOrThrow("type")),
            read = getInt(getColumnIndexOrThrow("read")) == 1,
            status = getInt(getColumnIndexOrThrow("status"))
        )
    }
}

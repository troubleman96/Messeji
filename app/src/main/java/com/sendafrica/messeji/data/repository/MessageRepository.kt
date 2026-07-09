package com.sendafrica.messeji.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.Telephony
import com.sendafrica.messeji.data.categorization.CategoryEngine
import com.sendafrica.messeji.data.categorization.MessageCategory
import com.sendafrica.messeji.data.db.dao.BackupRecordDao
import com.sendafrica.messeji.data.db.dao.BlockedNumberDao
import com.sendafrica.messeji.data.db.dao.MessageMetaDao
import com.sendafrica.messeji.data.db.dao.ThreadMetaDao
import com.sendafrica.messeji.data.db.entity.BackupRecord
import com.sendafrica.messeji.data.db.entity.BlockedNumber
import com.sendafrica.messeji.data.db.entity.MessageMeta
import com.sendafrica.messeji.data.db.entity.ThreadMeta
import com.sendafrica.messeji.data.sms.SmsManager
import com.sendafrica.messeji.data.sms.SmsMessage
import com.sendafrica.messeji.data.sms.SmsThread
import com.sendafrica.messeji.domain.ContactInfo
import com.sendafrica.messeji.domain.ThreadInfo
import com.sendafrica.messeji.util.ContactResolver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsManager: SmsManager,
    private val categoryEngine: CategoryEngine,
    private val messageMetaDao: MessageMetaDao,
    private val threadMetaDao: ThreadMetaDao,
    private val blockedNumberDao: BlockedNumberDao,
    private val backupRecordDao: BackupRecordDao,
    private val contactResolver: ContactResolver
) {
    private val contentResolver: ContentResolver = context.contentResolver

    suspend fun loadAllThreads(): List<ThreadInfo> {
        val threads = smsManager.getAllThreads()
        val blockedNumbers = blockedNumberDao.getAll().map { it.number }
        val pinnedIds = threadMetaDao.getPinnedThreadIds()
        val archivedIds = threadMetaDao.getArchivedThreadIds()
        val mutedIds = threadMetaDao.getMutedThreadIds()

        return threads
            .filter { it.address !in blockedNumbers }
            .map { thread ->
                val meta = threadMetaDao.getThreadMeta(thread.threadId)
                val contact = contactResolver.resolveContact(thread.address)
                val category = getThreadCategory(thread.threadId)
                ThreadInfo(
                    threadId = thread.threadId,
                    address = thread.address,
                    contactName = contact?.name ?: thread.address,
                    contactPhotoUri = contact?.photoUri,
                    snippet = thread.snippet,
                    date = thread.date,
                    messageCount = thread.messageCount,
                    read = thread.read,
                    category = category.value,
                    isPinned = thread.threadId in pinnedIds,
                    isMuted = thread.threadId in mutedIds,
                    isArchived = thread.threadId in archivedIds,
                    hasAttachment = thread.hasAttachment
                )
            }
            .sortedWith(compareByDescending<ThreadInfo> { it.isPinned }.thenByDescending { it.date })
    }

    suspend fun getThreadMessages(threadId: Long): List<SmsMessage> {
        return smsManager.getThreadMessages(threadId)
    }

    suspend fun sendMessage(address: String, body: String, simSlot: Int = 0): Boolean {
        return if (body.length > 160) {
            smsManager.sendMultipartSms(address, body, simSlot)
        } else {
            smsManager.sendSms(address, body, simSlot)
        }
    }

    suspend fun markThreadRead(threadId: Long) {
        smsManager.markThreadRead(threadId)
    }

    suspend fun deleteThread(threadId: Long) {
        smsManager.deleteThread(threadId)
        threadMetaDao.delete(threadId)
    }

    suspend fun deleteMessage(messageId: Long) {
        smsManager.deleteMessage(messageId)
        messageMetaDao.delete(messageId)
    }

    suspend fun togglePinThread(threadId: Long, pinned: Boolean) {
        val meta = threadMetaDao.getThreadMeta(threadId) ?: ThreadMeta(threadId = threadId)
        threadMetaDao.upsert(meta.copy(isPinned = pinned))
    }

    suspend fun toggleMuteThread(threadId: Long, muted: Boolean) {
        val meta = threadMetaDao.getThreadMeta(threadId) ?: ThreadMeta(threadId = threadId)
        threadMetaDao.upsert(meta.copy(isMuted = muted))
    }

    suspend fun archiveThread(threadId: Long) {
        val meta = threadMetaDao.getThreadMeta(threadId) ?: ThreadMeta(threadId = threadId)
        threadMetaDao.upsert(meta.copy(isArchived = true))
    }

    suspend fun unarchiveThread(threadId: Long) {
        threadMetaDao.setArchived(threadId, false)
    }

    suspend fun blockNumber(number: String) {
        blockedNumberDao.insert(BlockedNumber(number = number))
    }

    suspend fun unblockNumber(number: String) {
        blockedNumberDao.delete(number)
    }

    fun observeBlockedNumbers(): Flow<List<BlockedNumber>> {
        return blockedNumberDao.observeAll()
    }

    suspend fun isBlocked(number: String): Boolean {
        return blockedNumberDao.isBlocked(number) > 0
    }

    suspend fun getThreadCategory(threadId: Long): MessageCategory {
        val messages = smsManager.getThreadMessages(threadId)
        if (messages.isEmpty()) return MessageCategory.PERSON

        val lastMessage = messages.last()
        val isContact = contactResolver.isContact(lastMessage.address)

        val meta = messageMetaDao.getMessageMeta(lastMessage.id)
        if (meta != null) {
            return MessageCategory.fromValue(meta.category)
        }

        val category = categoryEngine.categorize(
            sender = lastMessage.address,
            body = lastMessage.body ?: "",
            isContact = isContact
        )

        messageMetaDao.upsert(
            MessageMeta(
                messageId = lastMessage.id,
                category = category.value
            )
        )

        return category
    }

    suspend fun recategorizeThread(threadId: Long, category: String) {
        val messages = smsManager.getThreadMessages(threadId)
        for (msg in messages) {
            messageMetaDao.upsert(
                MessageMeta(
                    messageId = msg.id,
                    category = category,
                    isManualOverride = true
                )
            )
        }
    }

    fun observeMessagesByCategory(category: String): Flow<List<Long>> {
        return messageMetaDao.getMessageIdsByCategory(category)
    }

    fun searchMessages(query: String): Flow<List<SmsMessage>> = flow {
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            "body LIKE ?",
            arrayOf("%$query%"),
            "date DESC"
        )
        val results = mutableListOf<SmsMessage>()
        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow("_id"))
                val threadId = c.getLong(c.getColumnIndexOrThrow("thread_id"))
                val address = c.getString(c.getColumnIndexOrThrow("address")) ?: ""
                val body = c.getString(c.getColumnIndexOrThrow("body"))
                val date = c.getLong(c.getColumnIndexOrThrow("date"))
                val type = c.getInt(c.getColumnIndexOrThrow("type"))
                val read = c.getInt(c.getColumnIndexOrThrow("read")) == 1
                val status = c.getInt(c.getColumnIndexOrThrow("status"))
                results.add(
                    SmsMessage(
                        id = id, threadId = threadId, address = address,
                        contactName = null, body = body, date = date,
                        dateSent = date, type = type, read = read, status = status
                    )
                )
            }
        }
        emit(results)
    }.flowOn(Dispatchers.IO)

    // Backup
    suspend fun createBackup(): Boolean {
        return try {
            val backupRecord = BackupRecord(
                sizeBytes = 0,
                location = "local",
                encrypted = true
            )
            backupRecordDao.insert(backupRecord)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun observeBackups(): Flow<List<BackupRecord>> {
        return backupRecordDao.observeAll()
    }

    suspend fun getLatestBackup(): BackupRecord? {
        return backupRecordDao.getLatest()
    }
}

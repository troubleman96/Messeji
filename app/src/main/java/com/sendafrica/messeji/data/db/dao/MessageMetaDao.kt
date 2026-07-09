package com.sendafrica.messeji.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sendafrica.messeji.data.db.entity.MessageMeta
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageMetaDao {

    @Query("SELECT * FROM message_meta WHERE messageId = :messageId")
    suspend fun getMessageMeta(messageId: Long): MessageMeta?

    @Query("SELECT category FROM message_meta WHERE messageId = :messageId")
    suspend fun getCategory(messageId: Long): String?

    @Query("SELECT messageId FROM message_meta WHERE category = :category")
    fun getMessageIdsByCategory(category: String): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: MessageMeta)

    @Query("UPDATE message_meta SET category = :category, isManualOverride = 1 WHERE messageId = :messageId")
    suspend fun updateCategory(messageId: Long, category: String)

    @Query("DELETE FROM message_meta WHERE messageId = :messageId")
    suspend fun delete(messageId: Long)

    @Query("DELETE FROM message_meta")
    suspend fun deleteAll()
}

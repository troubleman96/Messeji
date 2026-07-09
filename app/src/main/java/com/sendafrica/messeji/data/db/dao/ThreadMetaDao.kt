package com.sendafrica.messeji.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sendafrica.messeji.data.db.entity.ThreadMeta
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreadMetaDao {

    @Query("SELECT * FROM thread_meta WHERE threadId = :threadId")
    suspend fun getThreadMeta(threadId: Long): ThreadMeta?

    @Query("SELECT * FROM thread_meta WHERE threadId = :threadId")
    fun observeThreadMeta(threadId: Long): Flow<ThreadMeta?>

    @Query("SELECT threadId FROM thread_meta WHERE isPinned = 1")
    suspend fun getPinnedThreadIds(): List<Long>

    @Query("SELECT threadId FROM thread_meta WHERE isArchived = 1")
    suspend fun getArchivedThreadIds(): List<Long>

    @Query("SELECT threadId FROM thread_meta WHERE isMuted = 1")
    suspend fun getMutedThreadIds(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: ThreadMeta)

    @Query("UPDATE thread_meta SET isPinned = :pinned WHERE threadId = :threadId")
    suspend fun setPinned(threadId: Long, pinned: Boolean)

    @Query("UPDATE thread_meta SET isMuted = :muted WHERE threadId = :threadId")
    suspend fun setMuted(threadId: Long, muted: Boolean)

    @Query("UPDATE thread_meta SET isArchived = :archived WHERE threadId = :threadId")
    suspend fun setArchived(threadId: Long, archived: Boolean)

    @Query("UPDATE thread_meta SET defaultSimSlot = :simSlot WHERE threadId = :threadId")
    suspend fun setDefaultSimSlot(threadId: Long, simSlot: Int?)

    @Query("DELETE FROM thread_meta WHERE threadId = :threadId")
    suspend fun delete(threadId: Long)

    @Query("DELETE FROM thread_meta")
    suspend fun deleteAll()
}

package com.sendafrica.messeji.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thread_meta")
data class ThreadMeta(
    @PrimaryKey
    val threadId: Long,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val defaultSimSlot: Int? = null
)

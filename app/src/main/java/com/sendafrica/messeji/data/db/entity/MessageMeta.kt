package com.sendafrica.messeji.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_meta")
data class MessageMeta(
    @PrimaryKey
    val messageId: Long,
    val category: String = "person",
    val isManualOverride: Boolean = false
)

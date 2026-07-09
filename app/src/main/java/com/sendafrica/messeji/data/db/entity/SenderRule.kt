package com.sendafrica.messeji.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sender_rules")
data class SenderRule(
    @PrimaryKey
    val senderPattern: String,
    val forcedCategory: String,
    val createdBy: String = "user_override"
)

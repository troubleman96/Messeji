package com.sendafrica.messeji.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_numbers")
data class BlockedNumber(
    @PrimaryKey
    val number: String,
    val blockedAt: Long = System.currentTimeMillis()
)

package com.sendafrica.messeji.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_records")
data class BackupRecord(
    @PrimaryKey(autoGenerate = true)
    val backupId: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0,
    val location: String = "local",
    val encrypted: Boolean = true
)

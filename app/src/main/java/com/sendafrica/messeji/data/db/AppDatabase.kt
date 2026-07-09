package com.sendafrica.messeji.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sendafrica.messeji.data.db.dao.BackupRecordDao
import com.sendafrica.messeji.data.db.dao.BlockedNumberDao
import com.sendafrica.messeji.data.db.dao.MessageMetaDao
import com.sendafrica.messeji.data.db.dao.SenderRuleDao
import com.sendafrica.messeji.data.db.dao.ThreadMetaDao
import com.sendafrica.messeji.data.db.entity.BackupRecord
import com.sendafrica.messeji.data.db.entity.BlockedNumber
import com.sendafrica.messeji.data.db.entity.MessageMeta
import com.sendafrica.messeji.data.db.entity.SenderRule
import com.sendafrica.messeji.data.db.entity.ThreadMeta

@Database(
    entities = [
        MessageMeta::class,
        ThreadMeta::class,
        SenderRule::class,
        BlockedNumber::class,
        BackupRecord::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageMetaDao(): MessageMetaDao
    abstract fun threadMetaDao(): ThreadMetaDao
    abstract fun senderRuleDao(): SenderRuleDao
    abstract fun blockedNumberDao(): BlockedNumberDao
    abstract fun backupRecordDao(): BackupRecordDao
}

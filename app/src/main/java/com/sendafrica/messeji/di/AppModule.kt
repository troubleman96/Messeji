package com.sendafrica.messeji.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.sendafrica.messeji.data.AppSettings
import com.sendafrica.messeji.data.db.AppDatabase
import com.sendafrica.messeji.data.db.dao.BackupRecordDao
import com.sendafrica.messeji.data.db.dao.BlockedNumberDao
import com.sendafrica.messeji.data.db.dao.MessageMetaDao
import com.sendafrica.messeji.data.db.dao.SenderRuleDao
import com.sendafrica.messeji.data.db.dao.ThreadMetaDao
import com.sendafrica.messeji.data.repository.MessageRepository
import com.sendafrica.messeji.data.sms.SmsManager
import com.sendafrica.messeji.util.ContactResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "messeji_metadata.db"
        ).build()
    }

    @Provides
    fun provideMessageMetaDao(db: AppDatabase): MessageMetaDao = db.messageMetaDao()

    @Provides
    fun provideThreadMetaDao(db: AppDatabase): ThreadMetaDao = db.threadMetaDao()

    @Provides
    fun provideSenderRuleDao(db: AppDatabase): SenderRuleDao = db.senderRuleDao()

    @Provides
    fun provideBlockedNumberDao(db: AppDatabase): BlockedNumberDao = db.blockedNumberDao()

    @Provides
    fun provideBackupRecordDao(db: AppDatabase): BackupRecordDao = db.backupRecordDao()
}

package com.sendafrica.messeji.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sendafrica.messeji.data.db.entity.BackupRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupRecordDao {

    @Query("SELECT * FROM backup_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BackupRecord>>

    @Query("SELECT * FROM backup_records ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): BackupRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BackupRecord)

    @Query("DELETE FROM backup_records")
    suspend fun deleteAll()
}

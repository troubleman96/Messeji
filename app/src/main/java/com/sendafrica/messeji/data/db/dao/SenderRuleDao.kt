package com.sendafrica.messeji.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sendafrica.messeji.data.db.entity.SenderRule
import kotlinx.coroutines.flow.Flow

@Dao
interface SenderRuleDao {

    @Query("SELECT * FROM sender_rules WHERE senderPattern = :pattern")
    suspend fun getRule(pattern: String): SenderRule?

    @Query("SELECT * FROM sender_rules")
    fun observeAllRules(): Flow<List<SenderRule>>

    @Query("SELECT * FROM sender_rules WHERE createdBy = 'user_override'")
    suspend fun getUserOverrides(): List<SenderRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rule: SenderRule)

    @Query("DELETE FROM sender_rules WHERE senderPattern = :pattern")
    suspend fun delete(pattern: String)

    @Query("DELETE FROM sender_rules WHERE createdBy = 'system_default'")
    suspend fun deleteSystemRules()

    @Query("DELETE FROM sender_rules")
    suspend fun deleteAll()
}

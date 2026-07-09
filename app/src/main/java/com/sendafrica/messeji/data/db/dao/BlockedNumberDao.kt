package com.sendafrica.messeji.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sendafrica.messeji.data.db.entity.BlockedNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedNumberDao {

    @Query("SELECT * FROM blocked_numbers ORDER BY blockedAt DESC")
    fun observeAll(): Flow<List<BlockedNumber>>

    @Query("SELECT * FROM blocked_numbers ORDER BY blockedAt DESC")
    suspend fun getAll(): List<BlockedNumber>

    @Query("SELECT COUNT(*) FROM blocked_numbers WHERE number = :number")
    suspend fun isBlocked(number: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blocked: BlockedNumber)

    @Query("DELETE FROM blocked_numbers WHERE number = :number")
    suspend fun delete(number: String)
}

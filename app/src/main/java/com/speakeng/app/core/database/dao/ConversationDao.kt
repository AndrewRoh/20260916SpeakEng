package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.speakeng.app.core.database.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<ConversationEntity>>

    @Insert
    suspend fun insert(entity: ConversationEntity)

    @Query("SELECT COUNT(*) FROM conversations WHERE isFromUser = 0")
    suspend fun countAiReplies(): Int
}

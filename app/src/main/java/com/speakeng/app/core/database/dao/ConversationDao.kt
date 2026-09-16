package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.speakeng.app.core.database.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

/** TODO(Phase 4): wire this DAO into ConversationAiRepositoryImpl for local history persistence. */
@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<ConversationEntity>>

    @Insert
    suspend fun insert(entity: ConversationEntity)
}

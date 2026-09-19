package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.speakeng.app.core.database.entity.PronunciationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PronunciationHistoryDao {
    @Query("SELECT * FROM pronunciation_history ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<PronunciationHistoryEntity>>

    @Insert
    suspend fun insert(entity: PronunciationHistoryEntity)

    @Query("SELECT COUNT(*) FROM pronunciation_history")
    suspend fun countAll(): Int

    @Query("SELECT AVG(accuracy) FROM pronunciation_history")
    suspend fun averageAccuracy(): Double?
}

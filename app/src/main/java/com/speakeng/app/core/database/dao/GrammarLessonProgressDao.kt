package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.speakeng.app.core.database.entity.GrammarLessonProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrammarLessonProgressDao {
    @Query("SELECT * FROM grammar_lesson_progress")
    fun observeAll(): Flow<List<GrammarLessonProgressEntity>>

    @Upsert
    suspend fun upsert(entity: GrammarLessonProgressEntity)
}

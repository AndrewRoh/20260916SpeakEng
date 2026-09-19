package com.speakeng.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.speakeng.app.core.database.entity.CurriculumProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurriculumProgressDao {
    @Query("SELECT * FROM curriculum_progress")
    fun observeAll(): Flow<List<CurriculumProgressEntity>>

    @Upsert
    suspend fun upsert(entity: CurriculumProgressEntity)
}

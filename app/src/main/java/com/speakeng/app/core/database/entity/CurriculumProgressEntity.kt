package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "curriculum_progress")
data class CurriculumProgressEntity(
    @PrimaryKey val itemId: Long,
    val isCompleted: Boolean,
    val completedAt: Long?,
)

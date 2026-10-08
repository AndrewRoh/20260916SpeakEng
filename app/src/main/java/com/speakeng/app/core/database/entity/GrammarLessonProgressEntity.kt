package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar_lesson_progress")
data class GrammarLessonProgressEntity(
    @PrimaryKey val lessonId: Int,
    val isCompleted: Boolean,
    val completedAt: Long?,
)

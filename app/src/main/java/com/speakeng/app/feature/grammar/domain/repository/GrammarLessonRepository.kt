package com.speakeng.app.feature.grammar.domain.repository

import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState

interface GrammarLessonRepository {
    /** Seeds the track's start date on first call, then returns every lesson with its unlock/completion state. */
    suspend fun getLessons(): Result<List<GrammarLessonState>>
    suspend fun setCompleted(lessonId: Int, isCompleted: Boolean): Result<Unit>
}

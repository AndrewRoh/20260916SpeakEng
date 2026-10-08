package com.speakeng.app.feature.grammar.data.repository

import com.speakeng.app.core.database.dao.GrammarLessonProgressDao
import com.speakeng.app.core.database.dao.GrammarTrackStateDao
import com.speakeng.app.core.database.entity.GrammarLessonProgressEntity
import com.speakeng.app.core.database.entity.GrammarTrackStateEntity
import com.speakeng.app.feature.grammar.data.GrammarLessonContent
import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState
import com.speakeng.app.feature.grammar.domain.repository.GrammarLessonRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GrammarLessonRepositoryImpl @Inject constructor(
    private val trackStateDao: GrammarTrackStateDao,
    private val progressDao: GrammarLessonProgressDao,
) : GrammarLessonRepository {

    override suspend fun getLessons(): Result<List<GrammarLessonState>> = runCatching {
        val startedAtEpochDay = trackStateDao.get()?.startedAtEpochDay ?: seedStartDate()
        val daysElapsed = (LocalDate.now().toEpochDay() - startedAtEpochDay).coerceAtLeast(0)
        val unlockedCount = ((daysElapsed + 1) * LESSONS_PER_DAY)
            .coerceAtMost(GrammarLessonContent.LESSONS.size.toLong())
            .toInt()
        val completedIds = progressDao.observeAll().first()
            .filter { it.isCompleted }
            .map { it.lessonId }
            .toSet()

        GrammarLessonContent.LESSONS.map { lesson ->
            GrammarLessonState(
                lesson = lesson,
                isUnlocked = lesson.id <= unlockedCount,
                isCompleted = lesson.id in completedIds,
            )
        }
    }

    override suspend fun setCompleted(lessonId: Int, isCompleted: Boolean): Result<Unit> = runCatching {
        progressDao.upsert(
            GrammarLessonProgressEntity(
                lessonId = lessonId,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null,
            ),
        )
    }

    private suspend fun seedStartDate(): Long {
        val today = LocalDate.now().toEpochDay()
        trackStateDao.upsert(GrammarTrackStateEntity(startedAtEpochDay = today))
        return today
    }

    private companion object {
        const val LESSONS_PER_DAY = 2
    }
}

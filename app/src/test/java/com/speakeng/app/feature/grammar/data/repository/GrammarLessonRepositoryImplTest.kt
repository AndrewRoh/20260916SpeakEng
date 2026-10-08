package com.speakeng.app.feature.grammar.data.repository

import com.speakeng.app.core.database.dao.GrammarLessonProgressDao
import com.speakeng.app.core.database.dao.GrammarTrackStateDao
import com.speakeng.app.core.database.entity.GrammarLessonProgressEntity
import com.speakeng.app.core.database.entity.GrammarTrackStateEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GrammarLessonRepositoryImplTest {

    private class FakeGrammarTrackStateDao(initial: GrammarTrackStateEntity? = null) : GrammarTrackStateDao {
        var state: GrammarTrackStateEntity? = initial
        override suspend fun get(): GrammarTrackStateEntity? = state
        override suspend fun upsert(entity: GrammarTrackStateEntity) {
            state = entity
        }
    }

    private class FakeGrammarLessonProgressDao : GrammarLessonProgressDao {
        private val rows = MutableStateFlow<List<GrammarLessonProgressEntity>>(emptyList())
        override fun observeAll(): Flow<List<GrammarLessonProgressEntity>> = rows
        override suspend fun upsert(entity: GrammarLessonProgressEntity) {
            rows.value = rows.value.filterNot { it.lessonId == entity.lessonId } + entity
        }
    }

    @Test
    fun `first call seeds today as the start date and unlocks only the first 2 lessons`() = runTest {
        val trackStateDao = FakeGrammarTrackStateDao()
        val repository = GrammarLessonRepositoryImpl(trackStateDao, FakeGrammarLessonProgressDao())

        val lessons = repository.getLessons().getOrThrow()

        assertEquals(25, lessons.size)
        assertTrue(lessons[0].isUnlocked)
        assertTrue(lessons[1].isUnlocked)
        assertFalse(lessons[2].isUnlocked)
        assertEquals(LocalDate.now().toEpochDay(), trackStateDao.state?.startedAtEpochDay)
    }

    @Test
    fun `lessons unlock 2 per day as calendar days pass, capped at the lesson count`() = runTest {
        val startedFiveDaysAgo = LocalDate.now().minusDays(5).toEpochDay()
        val trackStateDao = FakeGrammarTrackStateDao(GrammarTrackStateEntity(startedAtEpochDay = startedFiveDaysAgo))
        val repository = GrammarLessonRepositoryImpl(trackStateDao, FakeGrammarLessonProgressDao())

        val lessons = repository.getLessons().getOrThrow()

        // day 0..5 elapsed => (5+1)*2 = 12 lessons unlocked
        assertTrue(lessons[11].isUnlocked)
        assertFalse(lessons[12].isUnlocked)
    }

    @Test
    fun `unlocked count never exceeds the full lesson list even after many days`() = runTest {
        val startedLongAgo = LocalDate.now().minusDays(100).toEpochDay()
        val trackStateDao = FakeGrammarTrackStateDao(GrammarTrackStateEntity(startedAtEpochDay = startedLongAgo))
        val repository = GrammarLessonRepositoryImpl(trackStateDao, FakeGrammarLessonProgressDao())

        val lessons = repository.getLessons().getOrThrow()

        assertTrue(lessons.all { it.isUnlocked })
    }

    @Test
    fun `setCompleted marks only the targeted lesson as completed`() = runTest {
        val trackStateDao = FakeGrammarTrackStateDao(GrammarTrackStateEntity(startedAtEpochDay = LocalDate.now().toEpochDay()))
        val progressDao = FakeGrammarLessonProgressDao()
        val repository = GrammarLessonRepositoryImpl(trackStateDao, progressDao)

        repository.setCompleted(lessonId = 1, isCompleted = true)
        val lessons = repository.getLessons().getOrThrow()

        assertTrue(lessons.first { it.lesson.id == 1 }.isCompleted)
        assertFalse(lessons.first { it.lesson.id == 2 }.isCompleted)
    }
}

package com.speakeng.app.feature.home.data.repository

import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.core.database.entity.CurriculumProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HomeRepositoryImplTest {

    private class FakeCurriculumProgressDao(initial: List<CurriculumProgressEntity> = emptyList()) : CurriculumProgressDao {
        private val state = MutableStateFlow(initial)
        override fun observeAll(): Flow<List<CurriculumProgressEntity>> = state
        override suspend fun upsert(entity: CurriculumProgressEntity) {
            state.value = state.value.filterNot { it.itemId == entity.itemId } + entity
        }
    }

    @Test
    fun `getHomeSummary counts only completed curriculum items`() = runTest {
        val dao = FakeCurriculumProgressDao(
            listOf(
                CurriculumProgressEntity(itemId = 1, isCompleted = true, completedAt = 1L),
                CurriculumProgressEntity(itemId = 2, isCompleted = false, completedAt = null),
            ),
        )
        val repository = HomeRepositoryImpl(dao)

        val summary = repository.getHomeSummary().getOrThrow()

        assertEquals(1, summary.completedLessons)
    }
}

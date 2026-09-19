package com.speakeng.app.feature.curriculum.data.repository

import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.core.database.entity.CurriculumProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CurriculumRepositoryImplTest {

    private class FakeCurriculumProgressDao : CurriculumProgressDao {
        private val state = MutableStateFlow<List<CurriculumProgressEntity>>(emptyList())

        override fun observeAll(): Flow<List<CurriculumProgressEntity>> = state

        override suspend fun upsert(entity: CurriculumProgressEntity) {
            state.value = state.value.filterNot { it.itemId == entity.itemId } + entity
        }
    }

    @Test
    fun `getCurriculum defaults every item to not completed`() = runTest {
        val repository = CurriculumRepositoryImpl(FakeCurriculumProgressDao())

        val items = repository.getCurriculum().getOrThrow()

        assertTrue(items.isNotEmpty())
        assertTrue(items.all { !it.isCompleted })
    }

    @Test
    fun `setCompleted marks only the targeted item as completed`() = runTest {
        val dao = FakeCurriculumProgressDao()
        val repository = CurriculumRepositoryImpl(dao)

        repository.setCompleted(itemId = 1, isCompleted = true)
        val items = repository.getCurriculum().getOrThrow()

        assertTrue(items.first { it.id == 1L }.isCompleted)
        assertFalse(items.first { it.id == 2L }.isCompleted)
    }

    @Test
    fun `setCompleted false clears a previously completed item`() = runTest {
        val dao = FakeCurriculumProgressDao()
        val repository = CurriculumRepositoryImpl(dao)
        repository.setCompleted(itemId = 1, isCompleted = true)

        repository.setCompleted(itemId = 1, isCompleted = false)

        val items = repository.getCurriculum().getOrThrow()
        assertFalse(items.first { it.id == 1L }.isCompleted)
    }
}

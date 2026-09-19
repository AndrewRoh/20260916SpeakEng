package com.speakeng.app.feature.curriculum.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import com.speakeng.app.feature.curriculum.domain.usecase.GetCurriculumUseCase
import com.speakeng.app.feature.curriculum.domain.usecase.SetCurriculumItemCompletedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CurriculumViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeCurriculumRepository(
        private var items: List<CurriculumItem> = listOf(CurriculumItem(1, "Greetings", "Beginner", false)),
    ) : CurriculumRepository {
        var lastSetCompleted: Pair<Long, Boolean>? = null

        override suspend fun getCurriculum(): Result<List<CurriculumItem>> = Result.success(items)

        override suspend fun setCompleted(itemId: Long, isCompleted: Boolean): Result<Unit> {
            lastSetCompleted = itemId to isCompleted
            items = items.map { if (it.id == itemId) it.copy(isCompleted = isCompleted) else it }
            return Result.success(Unit)
        }
    }

    private fun newViewModel(repository: FakeCurriculumRepository = FakeCurriculumRepository()) =
        CurriculumViewModel(GetCurriculumUseCase(repository), SetCurriculumItemCompletedUseCase(repository)) to repository

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        val (viewModel, _) = newViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }

    @Test
    fun `toggleCompleted flips the item and persists it`() = runTest(testDispatcher) {
        val (viewModel, repository) = newViewModel()
        advanceUntilIdle()

        viewModel.toggleCompleted(1)
        advanceUntilIdle()

        val data = (viewModel.uiState.value as UiState.Success).data
        assertTrue(data.first { it.id == 1L }.isCompleted)
        assertEquals(1L to true, repository.lastSetCompleted)
    }
}

package com.speakeng.app.feature.grammar.presentation.track

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.data.GrammarLessonContent
import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState
import com.speakeng.app.feature.grammar.domain.repository.GrammarLessonRepository
import com.speakeng.app.feature.grammar.domain.usecase.GetGrammarLessonsUseCase
import com.speakeng.app.feature.grammar.domain.usecase.SetGrammarLessonCompletedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GrammarTrackViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeGrammarLessonRepository : GrammarLessonRepository {
        var lessons = GrammarLessonContent.LESSONS.mapIndexed { index, lesson ->
            GrammarLessonState(lesson = lesson, isUnlocked = index < 2, isCompleted = false)
        }
        var lastSetCompleted: Pair<Int, Boolean>? = null

        override suspend fun getLessons(): Result<List<GrammarLessonState>> = Result.success(lessons)

        override suspend fun setCompleted(lessonId: Int, isCompleted: Boolean): Result<Unit> {
            lastSetCompleted = lessonId to isCompleted
            lessons = lessons.map { if (it.lesson.id == lessonId) it.copy(isCompleted = isCompleted) else it }
            return Result.success(Unit)
        }
    }

    private fun newViewModel(repository: FakeGrammarLessonRepository = FakeGrammarLessonRepository()) =
        GrammarTrackViewModel(GetGrammarLessonsUseCase(repository), SetGrammarLessonCompletedUseCase(repository)) to repository

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        val (viewModel, _) = newViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }

    @Test
    fun `toggleCompleted on an unlocked lesson flips it and persists`() = runTest(testDispatcher) {
        val (viewModel, repository) = newViewModel()
        advanceUntilIdle()

        viewModel.toggleCompleted(1)
        advanceUntilIdle()

        val data = (viewModel.uiState.value as UiState.Success).data
        assertTrue(data.first { it.lesson.id == 1 }.isCompleted)
        assertEquals(1 to true, repository.lastSetCompleted)
    }

    @Test
    fun `toggleCompleted on a locked lesson does nothing`() = runTest(testDispatcher) {
        val (viewModel, repository) = newViewModel()
        advanceUntilIdle()

        viewModel.toggleCompleted(3)
        advanceUntilIdle()

        val data = (viewModel.uiState.value as UiState.Success).data
        assertFalse(data.first { it.lesson.id == 3 }.isCompleted)
        assertEquals(null, repository.lastSetCompleted)
    }
}

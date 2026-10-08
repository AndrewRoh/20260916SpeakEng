package com.speakeng.app.feature.grammar.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.data.GrammarLessonContent
import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState
import com.speakeng.app.feature.grammar.domain.repository.GrammarLessonRepository
import com.speakeng.app.feature.grammar.domain.usecase.GetGrammarLessonsUseCase
import com.speakeng.app.feature.grammar.domain.usecase.SetGrammarLessonCompletedUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GrammarLessonDetailViewModelTest {

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
        var lastSetCompleted: Pair<Int, Boolean>? = null

        override suspend fun getLessons(): Result<List<GrammarLessonState>> = Result.success(
            GrammarLessonContent.LESSONS.mapIndexed { index, lesson ->
                GrammarLessonState(lesson = lesson, isUnlocked = index < 2, isCompleted = false)
            },
        )

        override suspend fun setCompleted(lessonId: Int, isCompleted: Boolean): Result<Unit> {
            lastSetCompleted = lessonId to isCompleted
            return Result.success(Unit)
        }
    }

    private class FakeSpeechRepository : SpeechRepository {
        var lastSpoken: String? = null
        override fun startListening(): Flow<SpeechRecognitionState> = emptyFlow()
        override fun stopListening() = Unit
        override fun speak(text: String, utteranceId: String, rate: Float) {
            lastSpoken = text
        }
        override fun stop() = Unit
        override fun ttsEvents(): Flow<SpeechEvent> = emptyFlow()
    }

    private fun newViewModel(
        lessonId: Int = 1,
        repository: FakeGrammarLessonRepository = FakeGrammarLessonRepository(),
        speechRepository: FakeSpeechRepository = FakeSpeechRepository(),
    ) = GrammarLessonDetailViewModel(
        SavedStateHandle(mapOf("lessonId" to lessonId)),
        GetGrammarLessonsUseCase(repository),
        SetGrammarLessonCompletedUseCase(repository),
        speechRepository,
        CoroutineScope(testDispatcher),
    ) to Pair(repository, speechRepository)

    private fun GrammarLessonDetailViewModel.data() = (uiState.value as UiState.Success).data

    @Test
    fun `loads the lesson matching the lessonId argument`() = runTest(testDispatcher) {
        val (viewModel, _) = newViewModel(lessonId = 2)
        advanceUntilIdle()

        assertEquals(2, viewModel.data().lesson.id)
        assertEquals(GrammarLessonContent.LESSONS[1].title, viewModel.data().lesson.title)
    }

    @Test
    fun `play speaks the example's English sentence and stop clears it`() = runTest(testDispatcher) {
        val (viewModel, repos) = newViewModel(lessonId = 1)
        val (_, speechRepository) = repos
        advanceUntilIdle()

        viewModel.play(0)
        assertEquals(0, viewModel.data().playingExampleIndex)
        assertEquals(GrammarLessonContent.LESSONS[0].examples[0].english, speechRepository.lastSpoken)

        viewModel.stop()
        assertNull(viewModel.data().playingExampleIndex)
    }

    @Test
    fun `toggleCompleted flips state and persists through the use case`() = runTest(testDispatcher) {
        val (viewModel, repos) = newViewModel(lessonId = 1)
        val (repository, _) = repos
        advanceUntilIdle()

        viewModel.toggleCompleted()
        advanceUntilIdle()

        assertEquals(true, viewModel.data().isCompleted)
        assertEquals(1 to true, repository.lastSetCompleted)
    }
}

package com.speakeng.app.feature.pronunciation.presentation

import android.net.Uri
import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationHistoryRecord
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.PronunciationHistoryRepository
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.pronunciation.domain.usecase.EvaluatePronunciationUseCase
import com.speakeng.app.feature.pronunciation.domain.usecase.SavePronunciationResultUseCase
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.BookSource
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import com.speakeng.app.feature.reading.domain.usecase.GetBooksUseCase
import com.speakeng.app.feature.reading.domain.usecase.LoadReadingSentencesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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

/**
 * Uses its own dispatcher (shared with `runTest`) rather than [com.speakeng.app.core.testing.MainDispatcherExtension],
 * because the success/error/permission-denied cases below need the ViewModel's
 * `viewModelScope.launch { speechRepository.startListening().collect { ... } }` coroutine to
 * actually run via `advanceUntilIdle()` — the shared extension deliberately uses an isolated
 * scheduler so other ViewModel tests can check only their synchronous initial state.
 */
class PronunciationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val book = Book("bundled:sample.txt", "sample", BookSource.BUNDLED, "sample.txt")

    private class FakeBookRepository(private val book: Book, private val content: String) : BookRepository {
        override fun getBooks(): Flow<List<Book>> = MutableStateFlow(listOf(book))
        override suspend fun getBookContent(bookId: String): Result<String> = Result.success(content)
        override suspend fun uploadBook(uri: Uri, fileName: String): Result<Book> = error("not used")
        override suspend fun deleteBook(bookId: String): Result<Unit> = error("not used")
    }

    private class FakeSpeechRepository : SpeechRepository {
        var recognitionFlow: Flow<SpeechRecognitionState> = emptyFlow()
        var stopListeningCalled = false
        override fun startListening(): Flow<SpeechRecognitionState> = recognitionFlow
        override fun stopListening() {
            stopListeningCalled = true
        }
        override fun speak(text: String, utteranceId: String, rate: Float) = Unit
        override fun stop() = Unit
        override fun ttsEvents(): Flow<SpeechEvent> = emptyFlow()
    }

    private class FakePronunciationHistoryRepository : PronunciationHistoryRepository {
        val savedRecords = mutableListOf<PronunciationHistoryRecord>()
        override suspend fun saveResult(record: PronunciationHistoryRecord): Result<Unit> {
            savedRecords.add(record)
            return Result.success(Unit)
        }
    }

    private fun newViewModel(
        bookRepository: BookRepository = FakeBookRepository(book, "Hello there."),
        speechRepository: FakeSpeechRepository = FakeSpeechRepository(),
        historyRepository: FakePronunciationHistoryRepository = FakePronunciationHistoryRepository(),
    ) = PronunciationViewModel(
        GetBooksUseCase(bookRepository),
        LoadReadingSentencesUseCase(bookRepository),
        EvaluatePronunciationUseCase(),
        SavePronunciationResultUseCase(historyRepository),
        speechRepository,
    ) to speechRepository

    private fun PronunciationViewModel.data() = (uiState.value as UiState.Success).data

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        val (viewModel, _) = newViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }

    @Test
    fun `startListening without permission does nothing`() = runTest(testDispatcher) {
        val (viewModel, speechRepository) = newViewModel()
        advanceUntilIdle()
        viewModel.selectBook(book.id)
        advanceUntilIdle()

        viewModel.startListening()

        assertFalse(viewModel.data().isListening)
        assertFalse(speechRepository.stopListeningCalled)
    }

    @Test
    fun `startListening flips isListening and a successful result is evaluated`() = runTest(testDispatcher) {
        val (viewModel, speechRepository) = newViewModel()
        advanceUntilIdle()
        viewModel.selectBook(book.id)
        advanceUntilIdle()
        viewModel.onMicPermissionResult(true)
        speechRepository.recognitionFlow = flowOf(
            SpeechRecognitionState.Listening,
            SpeechRecognitionState.Result("Hello there"),
        )

        viewModel.startListening()
        assertTrue(viewModel.data().isListening)

        advanceUntilIdle()

        val data = viewModel.data()
        assertFalse(data.isListening)
        assertEquals(100, data.result?.accuracy)
        assertEquals(listOf(100), data.sessionScores)
    }

    @Test
    fun `a successful result is saved to pronunciation history`() = runTest(testDispatcher) {
        val historyRepository = FakePronunciationHistoryRepository()
        val (viewModel, speechRepository) = newViewModel(historyRepository = historyRepository)
        advanceUntilIdle()
        viewModel.selectBook(book.id)
        advanceUntilIdle()
        viewModel.onMicPermissionResult(true)
        speechRepository.recognitionFlow = flowOf(SpeechRecognitionState.Result("Hello there"))

        viewModel.startListening()
        advanceUntilIdle()

        assertEquals(1, historyRepository.savedRecords.size)
        val record = historyRepository.savedRecords.first()
        assertEquals(book.id, record.bookId)
        assertEquals(0, record.sentenceIndex)
        assertEquals(100, record.accuracy)
    }

    @Test
    fun `a recognition error surfaces as an inline mic error without wiping the screen`() = runTest(testDispatcher) {
        val (viewModel, speechRepository) = newViewModel()
        advanceUntilIdle()
        viewModel.selectBook(book.id)
        advanceUntilIdle()
        viewModel.onMicPermissionResult(true)
        speechRepository.recognitionFlow = flowOf(SpeechRecognitionState.Error("No speech detected"))

        viewModel.startListening()
        advanceUntilIdle()

        val data = viewModel.data()
        assertFalse(data.isListening)
        assertEquals("No speech detected", data.micErrorMessage)
    }

    @Test
    fun `onMicPermissionResult records a denied permission`() = runTest(testDispatcher) {
        val (viewModel, _) = newViewModel()
        advanceUntilIdle()

        viewModel.onMicPermissionResult(false)

        assertFalse(viewModel.data().hasRecordPermission)
    }
}

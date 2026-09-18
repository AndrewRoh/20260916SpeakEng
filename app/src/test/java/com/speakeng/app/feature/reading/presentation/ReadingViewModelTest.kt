package com.speakeng.app.feature.reading.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import com.speakeng.app.feature.reading.domain.usecase.LoadReadingSentencesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ReadingViewModelTest {

    private class FakeBookRepository(private val content: Result<String>) : BookRepository {
        override fun getBooks(): Flow<List<Book>> = MutableStateFlow(emptyList())
        override suspend fun getBookContent(bookId: String): Result<String> = content
        override suspend fun uploadBook(uri: Uri, fileName: String): Result<Book> = error("not used")
        override suspend fun deleteBook(bookId: String): Result<Unit> = error("not used")
    }

    private class FakeSpeechRepository : SpeechRepository {
        override fun startListening(): Flow<String> = emptyFlow()
        override fun stopListening() = Unit
        override fun speak(text: String, utteranceId: String, rate: Float) = Unit
        override fun stop() = Unit
        override fun ttsEvents(): Flow<SpeechEvent> = emptyFlow()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = ReadingViewModel(
            savedStateHandle = SavedStateHandle(mapOf("bookId" to "bundled:sample.txt")),
            loadReadingSentencesUseCase = LoadReadingSentencesUseCase(
                FakeBookRepository(Result.success("Hello there. How are you?")),
            ),
            speechRepository = FakeSpeechRepository(),
        )

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }
}

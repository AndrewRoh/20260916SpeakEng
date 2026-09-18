package com.speakeng.app.feature.reading.presentation

import android.net.Uri
import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.BookSource
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import com.speakeng.app.feature.reading.domain.usecase.DeleteBookUseCase
import com.speakeng.app.feature.reading.domain.usecase.GetBooksUseCase
import com.speakeng.app.feature.reading.domain.usecase.UploadBookUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class BookListViewModelTest {

    private class FakeBookRepository(
        initialBooks: List<Book> = emptyList(),
    ) : BookRepository {
        private val books = MutableStateFlow(initialBooks)
        override fun getBooks(): Flow<List<Book>> = books
        override suspend fun getBookContent(bookId: String): Result<String> = Result.success("")
        override suspend fun uploadBook(uri: Uri, fileName: String): Result<Book> =
            Result.success(Book("uploaded:$fileName", fileName, BookSource.UPLOADED, fileName))
        override suspend fun deleteBook(bookId: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val repository = FakeBookRepository()
        val viewModel = BookListViewModel(
            GetBooksUseCase(repository),
            UploadBookUseCase(repository),
            DeleteBookUseCase(repository),
        )

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }
}

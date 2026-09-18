package com.speakeng.app.feature.reading.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.reading.domain.usecase.DeleteBookUseCase
import com.speakeng.app.feature.reading.domain.usecase.GetBooksUseCase
import com.speakeng.app.feature.reading.domain.usecase.UploadBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val uploadBookUseCase: UploadBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookListUiState>(UiState.Loading)
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBooksUseCase().collect { books ->
                _uiState.value = UiState.Success(books)
            }
        }
    }

    fun uploadBook(uri: Uri, fileName: String) {
        viewModelScope.launch {
            uploadBookUseCase(uri, fileName).onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Failed to upload the book")
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            deleteBookUseCase(bookId).onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Failed to delete the book")
            }
        }
    }
}

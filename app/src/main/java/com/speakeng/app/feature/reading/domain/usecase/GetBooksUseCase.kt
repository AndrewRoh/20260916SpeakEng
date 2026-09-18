package com.speakeng.app.feature.reading.domain.usecase

import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    operator fun invoke(): Flow<List<Book>> = bookRepository.getBooks()
}

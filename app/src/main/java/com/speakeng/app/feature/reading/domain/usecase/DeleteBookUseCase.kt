package com.speakeng.app.feature.reading.domain.usecase

import com.speakeng.app.feature.reading.domain.repository.BookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String): Result<Unit> = bookRepository.deleteBook(bookId)
}

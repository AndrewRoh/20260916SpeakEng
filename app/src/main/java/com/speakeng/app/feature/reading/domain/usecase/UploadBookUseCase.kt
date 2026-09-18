package com.speakeng.app.feature.reading.domain.usecase

import android.net.Uri
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import javax.inject.Inject

class UploadBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(uri: Uri, fileName: String): Result<Book> =
        bookRepository.uploadBook(uri, fileName)
}

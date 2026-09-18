package com.speakeng.app.feature.reading.domain.usecase

import com.speakeng.app.feature.reading.domain.model.ReadingSentence
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import com.speakeng.app.feature.reading.domain.util.SentenceSplitter
import javax.inject.Inject

class LoadReadingSentencesUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(bookId: String): Result<List<ReadingSentence>> =
        bookRepository.getBookContent(bookId).map { content ->
            SentenceSplitter.split(content).mapIndexed { index, text -> ReadingSentence(index, text) }
        }
}

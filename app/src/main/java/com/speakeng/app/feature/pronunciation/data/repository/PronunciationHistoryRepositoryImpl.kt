package com.speakeng.app.feature.pronunciation.data.repository

import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import com.speakeng.app.core.database.entity.PronunciationHistoryEntity
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationHistoryRecord
import com.speakeng.app.feature.pronunciation.domain.repository.PronunciationHistoryRepository
import javax.inject.Inject

class PronunciationHistoryRepositoryImpl @Inject constructor(
    private val pronunciationHistoryDao: PronunciationHistoryDao,
) : PronunciationHistoryRepository {

    override suspend fun saveResult(record: PronunciationHistoryRecord): Result<Unit> = runCatching {
        pronunciationHistoryDao.insert(
            PronunciationHistoryEntity(
                bookId = record.bookId,
                bookTitle = record.bookTitle,
                sentenceIndex = record.sentenceIndex,
                sentenceText = record.sentenceText,
                recognizedText = record.recognizedText,
                accuracy = record.accuracy,
                timestamp = record.timestamp,
            ),
        )
    }
}

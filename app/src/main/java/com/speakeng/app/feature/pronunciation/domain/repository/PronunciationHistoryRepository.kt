package com.speakeng.app.feature.pronunciation.domain.repository

import com.speakeng.app.feature.pronunciation.domain.model.PronunciationHistoryRecord

/** Persists pronunciation-practice results locally so progress survives app restarts. */
interface PronunciationHistoryRepository {
    suspend fun saveResult(record: PronunciationHistoryRecord): Result<Unit>
}

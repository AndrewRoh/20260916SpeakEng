package com.speakeng.app.feature.pronunciation.domain.usecase

import com.speakeng.app.feature.pronunciation.domain.model.PronunciationHistoryRecord
import com.speakeng.app.feature.pronunciation.domain.repository.PronunciationHistoryRepository
import javax.inject.Inject

class SavePronunciationResultUseCase @Inject constructor(
    private val pronunciationHistoryRepository: PronunciationHistoryRepository,
) {
    suspend operator fun invoke(record: PronunciationHistoryRecord): Result<Unit> =
        pronunciationHistoryRepository.saveResult(record)
}

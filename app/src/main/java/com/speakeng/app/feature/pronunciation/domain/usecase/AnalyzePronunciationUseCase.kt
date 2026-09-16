package com.speakeng.app.feature.pronunciation.domain.usecase

import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** TODO(Phase 3): replace the placeholder scoring below with real pronunciation analysis. */
class AnalyzePronunciationUseCase @Inject constructor(
    private val speechRepository: SpeechRepository,
) {
    suspend operator fun invoke(): Result<PronunciationResult> = runCatching {
        val recognizedText = speechRepository.startListening().first()
        PronunciationResult(recognizedText = recognizedText, score = 0, feedback = "Not scored yet")
    }
}

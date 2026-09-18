package com.speakeng.app.feature.pronunciation.domain.usecase

import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult
import com.speakeng.app.feature.pronunciation.domain.util.PronunciationEvaluator
import javax.inject.Inject

class EvaluatePronunciationUseCase @Inject constructor() {
    operator fun invoke(targetSentence: String, recognizedText: String): Result<PronunciationResult> =
        runCatching { PronunciationEvaluator.evaluate(targetSentence, recognizedText) }
}

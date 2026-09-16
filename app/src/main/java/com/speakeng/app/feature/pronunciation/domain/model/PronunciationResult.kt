package com.speakeng.app.feature.pronunciation.domain.model

/** TODO(Phase 3): fields will grow once real scoring (phoneme-level feedback) is added. */
data class PronunciationResult(
    val recognizedText: String,
    val score: Int,
    val feedback: String,
)

package com.speakeng.app.feature.pronunciation.domain.model

data class WordMatchResult(val word: String, val isCorrect: Boolean)

/**
 * [accuracy] (0..100) is a word-match reference score derived from comparing the STT transcript
 * against the target sentence — not a phoneme-level pronunciation assessment. Treat it as a rough
 * signal, not a precise grade.
 */
data class PronunciationResult(
    val recognizedText: String,
    val accuracy: Int,
    val wordResults: List<WordMatchResult>,
)

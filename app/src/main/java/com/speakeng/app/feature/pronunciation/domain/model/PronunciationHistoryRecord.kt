package com.speakeng.app.feature.pronunciation.domain.model

data class PronunciationHistoryRecord(
    val bookId: String,
    val bookTitle: String,
    val sentenceIndex: Int,
    val sentenceText: String,
    val recognizedText: String,
    val accuracy: Int,
    val timestamp: Long,
)

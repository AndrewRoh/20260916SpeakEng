package com.speakeng.app.feature.pronunciation.presentation

import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationResult
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.model.ReadingSentence

data class PronunciationData(
    val books: List<Book>,
    val selectedBook: Book?,
    val sentences: List<ReadingSentence>,
    val selectedSentenceIndex: Int,
    val isListening: Boolean,
    val isPlayingTts: Boolean,
    val ttsRate: Float,
    val isTtsRepeatEnabled: Boolean,
    val result: PronunciationResult?,
    val hasRecordPermission: Boolean,
    val sessionScores: List<Int>,
)

typealias PronunciationUiState = UiState<PronunciationData>

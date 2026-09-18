package com.speakeng.app.feature.reading.presentation

import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.reading.domain.model.ReadingSentence

data class ReadingData(
    val sentences: List<ReadingSentence>,
    val currentIndex: Int,
    val isPlaying: Boolean,
    val rate: Float,
    val isRepeatEnabled: Boolean,
)

typealias ReadingUiState = UiState<ReadingData>

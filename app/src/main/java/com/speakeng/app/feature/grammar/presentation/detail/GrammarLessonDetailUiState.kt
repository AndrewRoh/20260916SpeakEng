package com.speakeng.app.feature.grammar.presentation.detail

import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.domain.model.GrammarLesson

data class GrammarLessonDetailData(
    val lesson: GrammarLesson,
    val isCompleted: Boolean,
    val playingExampleIndex: Int?,
)

typealias GrammarLessonDetailUiState = UiState<GrammarLessonDetailData>

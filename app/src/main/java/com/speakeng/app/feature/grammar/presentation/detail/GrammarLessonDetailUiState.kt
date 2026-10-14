package com.speakeng.app.feature.grammar.presentation.detail

import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.domain.model.GrammarLesson

data class GrammarLessonDetailData(
    val lesson: GrammarLesson,
    val isCompleted: Boolean,
    val playingExampleIndex: Int?,
    val rate: Float = 1f,
    /** True only during the pause after a sentence, while "따라 말하기" mode waits for the learner to repeat it aloud. */
    val isAwaitingRepeat: Boolean = false,
    /** When true, finishing a sentence re-plays the same sentence instead of advancing. */
    val isSingleRepeatEnabled: Boolean = false,
    /** When true, finishing a sentence pauses (see [isAwaitingRepeat]) before advancing to the next one in a play-all run. */
    val isListenAndRepeatEnabled: Boolean = false,
    /** True while a "전체 듣기"/"랜덤 듣기" run is auto-advancing through multiple sentences. */
    val isSequencePlaying: Boolean = false,
)

typealias GrammarLessonDetailUiState = UiState<GrammarLessonDetailData>

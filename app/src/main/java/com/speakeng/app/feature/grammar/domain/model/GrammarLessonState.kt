package com.speakeng.app.feature.grammar.domain.model

data class GrammarLessonState(
    val lesson: GrammarLesson,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
)

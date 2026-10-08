package com.speakeng.app.feature.grammar.domain.model

data class GrammarExample(val english: String, val korean: String)

data class GrammarLesson(
    val id: Int,
    val dayNumber: Int,
    val title: String,
    val explanation: String,
    val examples: List<GrammarExample>,
)

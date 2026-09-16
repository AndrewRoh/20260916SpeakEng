package com.speakeng.app.feature.curriculum.domain.model

data class CurriculumItem(
    val id: Long,
    val title: String,
    val level: String,
    val isCompleted: Boolean,
)

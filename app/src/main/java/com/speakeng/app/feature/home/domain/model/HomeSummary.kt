package com.speakeng.app.feature.home.domain.model

/** Snapshot of the learner's progress shown on the home dashboard. */
data class HomeSummary(
    val userName: String,
    val streakDays: Int,
    val todayGoalProgress: Float,
    val completedLessons: Int,
)

package com.speakeng.app.feature.profile.domain.model

data class UserProfile(
    val userName: String,
    val totalConversations: Int,
    val totalMinutesPracticed: Int,
    val averagePronunciationScore: Int,
)

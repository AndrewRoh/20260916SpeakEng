package com.speakeng.app.feature.profile.domain.repository

import com.speakeng.app.feature.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getUserProfile(): Result<UserProfile>
}

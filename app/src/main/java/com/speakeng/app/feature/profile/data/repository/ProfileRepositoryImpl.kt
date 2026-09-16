package com.speakeng.app.feature.profile.data.repository

import com.speakeng.app.feature.profile.domain.model.UserProfile
import com.speakeng.app.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

/** TODO(Phase 4/5): source real stats from Room, and sync with Firebase in Phase 5. */
class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
    override suspend fun getUserProfile(): Result<UserProfile> = Result.success(
        UserProfile(
            userName = "Learner",
            totalConversations = 0,
            totalMinutesPracticed = 0,
            averagePronunciationScore = 0,
        ),
    )
}

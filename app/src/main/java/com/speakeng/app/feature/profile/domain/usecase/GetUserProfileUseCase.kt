package com.speakeng.app.feature.profile.domain.usecase

import com.speakeng.app.feature.profile.domain.model.UserProfile
import com.speakeng.app.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): Result<UserProfile> = profileRepository.getUserProfile()
}

package com.speakeng.app.feature.profile.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.profile.domain.model.UserProfile
import com.speakeng.app.feature.profile.domain.repository.ProfileRepository
import com.speakeng.app.feature.profile.domain.usecase.GetUserProfileUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ProfileViewModelTest {

    private class FakeProfileRepository : ProfileRepository {
        override suspend fun getUserProfile(): Result<UserProfile> =
            Result.success(UserProfile("Alex", totalConversations = 4, totalMinutesPracticed = 20, averagePronunciationScore = 80))
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = ProfileViewModel(GetUserProfileUseCase(FakeProfileRepository()))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }
}

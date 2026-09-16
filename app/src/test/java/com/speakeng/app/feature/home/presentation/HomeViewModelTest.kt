package com.speakeng.app.feature.home.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.home.domain.model.HomeSummary
import com.speakeng.app.feature.home.domain.repository.HomeRepository
import com.speakeng.app.feature.home.domain.usecase.GetHomeSummaryUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class HomeViewModelTest {

    private class FakeHomeRepository : HomeRepository {
        override suspend fun getHomeSummary(): Result<HomeSummary> =
            Result.success(HomeSummary(userName = "Alex", streakDays = 3, todayGoalProgress = 0.5f, completedLessons = 2))
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = HomeViewModel(GetHomeSummaryUseCase(FakeHomeRepository()))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }
}

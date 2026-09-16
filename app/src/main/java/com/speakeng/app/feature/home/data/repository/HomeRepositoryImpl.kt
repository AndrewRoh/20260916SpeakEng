package com.speakeng.app.feature.home.data.repository

import com.speakeng.app.feature.home.domain.model.HomeSummary
import com.speakeng.app.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Phase 1 stub: returns hardcoded placeholder data.
 * TODO(Phase 4): back this with the local Room database once curriculum/progress
 * tracking is implemented.
 */
class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    override suspend fun getHomeSummary(): Result<HomeSummary> = Result.success(
        HomeSummary(
            userName = "Learner",
            streakDays = 0,
            todayGoalProgress = 0f,
            completedLessons = 0,
        ),
    )
}

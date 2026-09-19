package com.speakeng.app.feature.home.data.repository

import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.feature.home.domain.model.HomeSummary
import com.speakeng.app.feature.home.domain.repository.HomeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** TODO(Phase 5): back streakDays/todayGoalProgress with real per-day activity tracking. */
class HomeRepositoryImpl @Inject constructor(
    private val curriculumProgressDao: CurriculumProgressDao,
) : HomeRepository {
    override suspend fun getHomeSummary(): Result<HomeSummary> = runCatching {
        val completedLessons = curriculumProgressDao.observeAll().first().count { it.isCompleted }
        HomeSummary(
            userName = "Learner",
            streakDays = 0,
            todayGoalProgress = 0f,
            completedLessons = completedLessons,
        )
    }
}

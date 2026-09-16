package com.speakeng.app.feature.curriculum.data.repository

import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import javax.inject.Inject

/** TODO(Phase 4): read/write curriculum progress through the local Room database. */
class CurriculumRepositoryImpl @Inject constructor() : CurriculumRepository {
    override suspend fun getCurriculum(): Result<List<CurriculumItem>> = Result.success(
        listOf(
            CurriculumItem(id = 1, title = "Everyday Greetings", level = "Beginner", isCompleted = false),
            CurriculumItem(id = 2, title = "Ordering Food", level = "Beginner", isCompleted = false),
            CurriculumItem(id = 3, title = "Small Talk at Work", level = "Intermediate", isCompleted = false),
        ),
    )
}

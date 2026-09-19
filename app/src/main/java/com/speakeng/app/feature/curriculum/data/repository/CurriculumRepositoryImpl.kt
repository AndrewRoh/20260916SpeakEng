package com.speakeng.app.feature.curriculum.data.repository

import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.core.database.entity.CurriculumProgressEntity
import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/** The curriculum's items/levels are a fixed catalog for now; only completion state is persisted. */
private val CURRICULUM_CATALOG = listOf(
    CurriculumItem(id = 1, title = "Everyday Greetings", level = "Beginner", isCompleted = false),
    CurriculumItem(id = 2, title = "Ordering Food", level = "Beginner", isCompleted = false),
    CurriculumItem(id = 3, title = "Small Talk at Work", level = "Intermediate", isCompleted = false),
)

class CurriculumRepositoryImpl @Inject constructor(
    private val curriculumProgressDao: CurriculumProgressDao,
) : CurriculumRepository {

    override suspend fun getCurriculum(): Result<List<CurriculumItem>> = runCatching {
        val completedIds = curriculumProgressDao.observeAll().first()
            .filter { it.isCompleted }
            .map { it.itemId }
            .toSet()
        CURRICULUM_CATALOG.map { it.copy(isCompleted = it.id in completedIds) }
    }

    override suspend fun setCompleted(itemId: Long, isCompleted: Boolean): Result<Unit> = runCatching {
        curriculumProgressDao.upsert(
            CurriculumProgressEntity(
                itemId = itemId,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null,
            ),
        )
    }
}

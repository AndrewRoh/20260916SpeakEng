package com.speakeng.app.feature.curriculum.domain.repository

import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem

interface CurriculumRepository {
    suspend fun getCurriculum(): Result<List<CurriculumItem>>
    suspend fun setCompleted(itemId: Long, isCompleted: Boolean): Result<Unit>
}

package com.speakeng.app.feature.curriculum.domain.usecase

import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import javax.inject.Inject

class SetCurriculumItemCompletedUseCase @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
) {
    suspend operator fun invoke(itemId: Long, isCompleted: Boolean): Result<Unit> =
        curriculumRepository.setCompleted(itemId, isCompleted)
}

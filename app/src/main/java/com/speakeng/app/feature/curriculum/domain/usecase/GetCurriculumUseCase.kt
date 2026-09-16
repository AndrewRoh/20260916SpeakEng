package com.speakeng.app.feature.curriculum.domain.usecase

import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import javax.inject.Inject

class GetCurriculumUseCase @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
) {
    suspend operator fun invoke(): Result<List<CurriculumItem>> = curriculumRepository.getCurriculum()
}
